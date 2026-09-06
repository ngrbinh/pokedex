package com.pokedex.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("pokedex")
/**
 * Binds application configuration under the {@code pokedex} prefix.
 */
public class PokedexProperties {

    private PokeApi pokeApi = new PokeApi();
    private Page page = new Page();
    private Cache cache = new Cache();

    public PokeApi getPokeApi() {
        return pokeApi;
    }

    public Page getPage() {
        return page;
    }

    public Cache getCache() {
        return cache;
    }

    /**
     * PokeAPI connection and retry settings.
     */
    public static class PokeApi {
        private String baseUrl;
        private Duration connectTimeout;
        private Duration readTimeout;
        private int retries;
        private Duration retryInitialDelay;

        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String value) { baseUrl = value; }
        public Duration getConnectTimeout() { return connectTimeout; }
        public void setConnectTimeout(Duration value) { connectTimeout = value; }
        public Duration getReadTimeout() { return readTimeout; }
        public void setReadTimeout(Duration value) { readTimeout = value; }
        public int getRetries() { return retries; }
        public void setRetries(int value) { retries = value; }
        public Duration getRetryInitialDelay() { return retryInitialDelay; }
        public void setRetryInitialDelay(Duration value) { retryInitialDelay = value; }
    }

    /**
     * Default and maximum page-size settings.
     */
    public static class Page {
        private int defaultSize;
        private int maxSize;

        public int getDefaultSize() { return defaultSize; }
        public void setDefaultSize(int value) { defaultSize = value; }
        public int getMaxSize() { return maxSize; }
        public void setMaxSize(int value) { maxSize = value; }
    }

    /**
     * Cache lifetime settings.
     */
    public static class Cache {
        private Duration ttl;

        public Duration getTtl() { return ttl; }
        public void setTtl(Duration value) { ttl = value; }
    }
}