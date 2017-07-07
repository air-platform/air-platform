package net.aircommunity.platform.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.aggregate.AggregateMetricReader;
import org.springframework.boot.actuate.metrics.export.MetricExportProperties;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jmx.export.MBeanExporter;

/**
 * Redis configuration for application.
 * 
 * @author Bin.Zhang
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedisConfig extends CachingConfigurerSupport {
	private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

	@Resource
	private RedisConnectionFactory redisConnectionFactory;

	@Resource
	private MetricExportProperties export;

	@Bean
	public RedisTemplate<String, String> counterTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> stringTemplate = new RedisTemplate<>();
		stringTemplate.setConnectionFactory(redisConnectionFactory);
		stringTemplate.setDefaultSerializer(new StringRedisSerializer());
		stringTemplate.afterPropertiesSet();
		return stringTemplate;
	}

	@Bean
	@ExportMetricWriter
	// metrics are exported to a Redis cache for aggregation
	public RedisMetricRepository redisMetricWriter() {
		return new RedisMetricRepository(redisConnectionFactory, export.getRedis().getPrefix(),
				export.getRedis().getKey());
	}

	@Bean
	@ExportMetricWriter
	// metrics are exported as MBeans to the local server
	public JmxMetricWriter jmxMetricWriter(@Qualifier("mbeanExporter") MBeanExporter exporter) {
		return new JmxMetricWriter(exporter);
	}

	@Bean
	public PublicMetrics metricsAggregate() {
		return new MetricReaderPublicMetrics(aggregatesMetricReader());
	}

	// The MetricReaders blew are not @Beans and are not marked as @ExportMetricReader because they are just collecting
	// and analyzing data from other repositories, and don’t want to export their values.
	private MetricReader aggregatesMetricReader() {
		AggregateMetricReader repository = new AggregateMetricReader(globalMetricsForAggregation());
		repository.setKeyPattern(export.getAggregate().getKeyPattern());
		return repository;
	}

	private MetricReader globalMetricsForAggregation() {
		return new RedisMetricRepository(redisConnectionFactory, export.getRedis().getAggregatePrefix(),
				export.getRedis().getKey());
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new CacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
				LOG.warn("[Redis offline] handleCacheGetError in redis: {}", exception.getMessage());
			}

			@Override
			public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
				LOG.warn("[Redis offline] handleCachePutError in redis: {}", exception.getMessage());
			}

			@Override
			public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
				LOG.warn("[Redis offline] handleCacheEvictError in redis: {}", exception.getMessage());
			}

			@Override
			public void handleCacheClearError(RuntimeException exception, Cache cache) {
				LOG.warn("handleCacheClearError in redis: {}", exception.getMessage());
			}
		};
	}
}
