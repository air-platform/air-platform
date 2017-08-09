package net.aircommunity.platform.model;

import net.aircommunity.platform.Configuration;
import net.aircommunity.platform.rest.UeditorResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Created by kongxiangwen on 8/8/17.
 */
@Component
public class UeditorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(UeditorConfig.class);

    private String imageUrl;
    private String imageActionName;
    private String imageUrlPrefix;
    private String imageFieldName;
    private int imageMaxSize;
    private String [] imageAllowFiles;
    private Boolean imageCompressEnable;
    private int imageCompressBorder;
    private String imageInsertAlign;
    private String imagePathFormat;


    private String scrawlActionName;
    private String scrawlFieldName;
    private String scrawlPathFormat;
    private int scrawlMaxSize;
    private String scrawlUrlPrefix;
    private String scrawlInsertAlign;

    @Resource
    private Configuration configuration;


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageActionName() {
        return imageActionName;
    }

    public void setImageActionName(String imageActionName) {
        this.imageActionName = imageActionName;
    }

    public String getImageUrlPrefix() {
        return imageUrlPrefix;
    }

    public void setImageUrlPrefix(String imageUrlPrefix) {
        this.imageUrlPrefix = imageUrlPrefix;
    }


    public String getImageFieldName() {
        return imageFieldName;
    }

    public void setImageFieldName(String imageFieldName) {
        this.imageFieldName = imageFieldName;
    }

    public int getImageMaxSize() {
        return imageMaxSize;
    }

    public void setImageMaxSize(int imageMaxSize) {
        this.imageMaxSize = imageMaxSize;
    }

    public String [] getImageAllowFiles() {
        return imageAllowFiles;
    }

    public void setImageAllowFiles(String [] imageAllowFiles) {
        this.imageAllowFiles = imageAllowFiles;
    }

    public Boolean getImageCompressEnable() {
        return imageCompressEnable;
    }

    public void setImageCompressEnable(Boolean imageCompressEnable) {
        this.imageCompressEnable = imageCompressEnable;
    }

    public int getImageCompressBorder() {
        return imageCompressBorder;
    }

    public void setImageCompressBorder(int imageCompressBorder) {
        this.imageCompressBorder = imageCompressBorder;
    }

    public String getImageInsertAlign() {
        return imageInsertAlign;
    }

    public void setImageInsertAlign(String imageInsertAlign) {
        this.imageInsertAlign = imageInsertAlign;
    }

    public String getImagePathFormat() {
        return imagePathFormat;
    }

    public void setImagePathFormat(String imagePathFormat) {
        this.imagePathFormat = imagePathFormat;
    }

    public String getScrawlActionName() {
        return scrawlActionName;
    }

    public void setScrawlActionName(String scrawlActionName) {
        this.scrawlActionName = scrawlActionName;
    }

    public String getScrawlFieldName() {
        return scrawlFieldName;
    }

    public void setScrawlFieldName(String scrawlFieldName) {
        this.scrawlFieldName = scrawlFieldName;
    }

    public String getScrawlPathFormat() {
        return scrawlPathFormat;
    }

    public void setScrawlPathFormat(String scrawlPathFormat) {
        this.scrawlPathFormat = scrawlPathFormat;
    }

    public int getScrawlMaxSize() {
        return scrawlMaxSize;
    }

    public void setScrawlMaxSize(int scrawlMaxSize) {
        this.scrawlMaxSize = scrawlMaxSize;
    }

    public String getScrawlUrlPrefix() {
        return scrawlUrlPrefix;
    }

    public void setScrawlUrlPrefix(String scrawlUrlPrefix) {
        this.scrawlUrlPrefix = scrawlUrlPrefix;
    }

    public String getScrawlInsertAlign() {
        return scrawlInsertAlign;
    }

    public void setScrawlInsertAlign(String scrawlInsertAlign) {
        this.scrawlInsertAlign = scrawlInsertAlign;
    }

    @PostConstruct
    private void init() {
        setImageUrl(configuration.getUeditorImageUrl());
        setImageActionName(configuration.getUeditorImageActionName());
        setImageUrlPrefix(configuration.getUeditorImageUrlPrefix());
        setImageFieldName(configuration.getUeditorImageFieldName());
        setImageMaxSize(configuration.getUeditorImageMaxSize());
        setImageAllowFiles(configuration.getUeditorImageAllowFiles().split(","));
        setImageCompressEnable(configuration.isUeditorImageCompressEnable());
        setImageCompressBorder(configuration.getUeditorImageCompressBorder());
        setImageInsertAlign(configuration.getUeditorImageInsertAlign());
        setImagePathFormat(configuration.getUeditorImagePathFormat());

        setScrawlActionName(configuration.getUeditorScrawlActionName());
        setScrawlFieldName(configuration.getUeditorScrawlFieldName());
        setScrawlPathFormat(configuration.getUeditorScrawlPathFormat());
        setScrawlMaxSize(configuration.getUeditorScrawlMaxSize());
        setScrawlUrlPrefix(configuration.getUeditorScrawlUrlPrefix());
        setScrawlInsertAlign(configuration.getUeditorScrawlInsertAlign());

    }

}
