package net.aircommunity.platform.service.internal;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micro.common.io.MoreFiles;
import net.aircommunity.platform.AirException;
import net.aircommunity.platform.Codes;
import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.model.AirJet;
import net.aircommunity.platform.model.Page;
import net.aircommunity.platform.nls.M;
import net.aircommunity.platform.repository.AirJetRepository;
import net.aircommunity.platform.service.AirJetService;

/**
 * AirJet service implementation.
 * 
 * @author Bin.Zhang
 */
@Service
@Transactional
public class AirJetServiceImpl extends AbstractServiceSupport implements AirJetService {
	private static final Logger LOG = LoggerFactory.getLogger(AirJetServiceImpl.class);

	private static final String CACHE_NAME = "cache.airjet";
	private static final String AIRJETS_INFO = "data/airjet.json";
	// FORMAT: http://host:port/airjet/image/xxx.jpeg
	// private static final String BASE_URL_FORMAT = "http://%s:%d/%s";

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private Configuration configuration;

	@Resource
	private AirJetRepository airjetRepository;

	@PostConstruct
	private void init() {
		try {
			String json = MoreFiles.toString(AIRJETS_INFO);
			if (json == null) {
				LOG.warn("Not airjet information provided, skip importing jetair info to DB");
				return;
			}
			List<AirJet> airjets = objectMapper.readValue(json, new TypeReference<List<AirJet>>() {
			});
			for (AirJet jet : airjets) {
				// XXX NOT USE LOCAL FILE
				// jet.setImage(String.format(BASE_URL_FORMAT, configuration.getPublicHost(),
				// configuration.getPublicPort(), jet.getImage()));
				AirJet found = airjetRepository.findByTypeIgnoreCase(jet.getType());
				if (found == null) {
					createAirJet(jet);
				}
				else {
					updateAirJet(found.getId(), jet);
				}
			}
		}
		catch (Exception e) {
			LOG.warn("Failed to initialize airjet information, casue: " + e.getMessage(), e);
		}
	}

	@Override
	public AirJet createAirJet(AirJet airJet) {
		AirJet airJetExisting = airjetRepository.findByTypeIgnoreCase(airJet.getType());
		if (airJetExisting != null) {
			throw new AirException(Codes.AIRJET_ALREADY_EXISTS, M.msg(M.AIRJET_ALREADY_EXISTS, airJet.getType()));
		}
		AirJet newAirJet = new AirJet();
		copyProperties(airJet, newAirJet);
		return safeExecute(() -> airjetRepository.save(newAirJet), "Create airJet %s failed", airJet);
	}

	private void copyProperties(AirJet src, AirJet tgt) {
		tgt.setName(src.getName());
		tgt.setCapacity(src.getCapacity());
		tgt.setDescription(src.getDescription());
		tgt.setFullloadRange(src.getFullloadRange());
		tgt.setImage(src.getImage());
		tgt.setWeight(src.getWeight());
		tgt.setType(src.getType());
	}

	@Cacheable(cacheNames = CACHE_NAME)
	@Override
	public AirJet findAirJet(String airJetId) {
		AirJet airJet = airjetRepository.findOne(airJetId);
		if (airJet == null) {
			throw new AirException(Codes.AIRJET_NOT_FOUND, M.msg(M.AIRJET_NOT_FOUND, airJetId));
		}
		return airJet;
	}

	@Override
	public AirJet findAirJetByType(String type) {
		AirJet airJet = airjetRepository.findByTypeIgnoreCase(type);
		if (airJet == null) {
			throw new AirException(Codes.AIRJET_NOT_FOUND, M.msg(M.AIRJET_TYPE_NOT_FOUND, type));
		}
		return airJet;
	}

	@CachePut(cacheNames = CACHE_NAME, key = "#airJetId")
	@Override
	public AirJet updateAirJet(String airJetId, AirJet newAirJet) {
		AirJet airJet = findAirJet(airJetId);
		AirJet airJetExisting = airjetRepository.findByTypeIgnoreCase(newAirJet.getType());
		if (airJetExisting != null && !airJetExisting.getId().equals(airJetId)) {
			throw new AirException(Codes.AIRJET_ALREADY_EXISTS, M.msg(M.AIRJET_ALREADY_EXISTS, airJet.getType()));
		}
		copyProperties(newAirJet, airJet);
		return safeExecute(() -> airjetRepository.save(airJet), "Update airJet %s to %s failed", airJetId, airJet);
	}

	@Override
	public Page<AirJet> listAirJets(int page, int pageSize) {
		return Pages.adapt(airjetRepository.findAll(Pages.createPageRequest(page, pageSize)));
	}

	@CacheEvict(cacheNames = CACHE_NAME, key = "#airJetId")
	@Override
	public void deleteAirJet(String airJetId) {
		safeExecute(() -> airjetRepository.delete(airJetId), "Delete airJet %s failed", airJetId);
	}

	@CacheEvict(cacheNames = CACHE_NAME, allEntries = true)
	@Override
	public void deleteAirJets() {
		safeExecute(() -> airjetRepository.deleteAll(), "Update all airJets failed");
	}

}
