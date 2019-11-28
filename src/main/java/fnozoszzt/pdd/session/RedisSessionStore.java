package fnozoszzt.pdd.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

public class RedisSessionStore implements SessionStore {
    private static final Logger logger = LoggerFactory.getLogger(RedisSessionStore.class);
    private static final int MAX_RETRY_TIME = 3;
    private RedisTemplate<String, Object> redisTemplate;

    public RedisSessionStore(JedisConnectionFactory jedisConnectionFactory) {
        if (jedisConnectionFactory == null) {
            throw new IllegalArgumentException("jedisConnectionFactory is null");
        } else {
            this.redisTemplate = new RedisTemplate();
            this.redisTemplate.setConnectionFactory(jedisConnectionFactory);
            this.redisTemplate.setKeySerializer(this.redisTemplate.getStringSerializer());
            this.redisTemplate.afterPropertiesSet();
        }
    }

    /** @deprecated */
    @Deprecated
    public RedisSessionStore(String redisUrl) {
        this(redisUrl, (JedisPoolConfig)null);
    }

    /** @deprecated */
    @Deprecated
    public RedisSessionStore(String redisUrl, String password, JedisPoolConfig config) {
        this(String.format("%s?password=%s", redisUrl, password), config);
    }

    /** @deprecated */
    @Deprecated
    public RedisSessionStore(String redisUrl, JedisPoolConfig config) {
        RedisConnectionProp prop = RedisConnectionProp.newConfig(redisUrl);
        if (config == null) {
            config = this.getDefaultConfig();
        }

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(prop.getHost());
        jedisConnectionFactory.setPort(prop.getPort());
        if (StringUtils.isNotBlank(prop.getPassword())) {
            jedisConnectionFactory.setPassword(prop.getPassword());
        }

        jedisConnectionFactory.setPoolConfig(config);
        jedisConnectionFactory.afterPropertiesSet();
        this.redisTemplate = new RedisTemplate();
        this.redisTemplate.setConnectionFactory(jedisConnectionFactory);
        this.redisTemplate.setKeySerializer(this.redisTemplate.getStringSerializer());
        this.redisTemplate.afterPropertiesSet();
    }

    private JedisPoolConfig getDefaultConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(128);
        config.setMinIdle(32);
        config.setMaxActive(512);
        config.setMaxWait(5000L);
        config.setMinEvictableIdleTimeMillis(300000L);
        config.setNumTestsPerEvictionRun(3);
        config.setTimeBetweenEvictionRunsMillis(60000L);
        config.setWhenExhaustedAction((byte)1);
        return config;
    }

    public void shutdown() {
        if (this.redisTemplate != null && this.redisTemplate.getConnectionFactory() != null && this.redisTemplate.getConnectionFactory().getConnection() != null) {
            this.redisTemplate.getConnectionFactory().getConnection().close();
        }

    }

    public <V extends Serializable> V get(final String sessionId, final String key) {
        return this.execute(new RedisSessionStore.HashOperationsCallback<V>() {
            public V doOperations() {
                HashOperations<String, String, Object> ops = RedisSessionStore.this.redisTemplate.opsForHash();
                V value = (V)ops.get(sessionId, key);
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ HGET {}.{} -> {}", new Object[]{sessionId, key, value});
                }

                return value;
            }
        });
    }

    public <V extends Serializable> List<V> get(final String sessionId, final List<String> hashKeys) {
        return (List)this.execute(new RedisSessionStore.HashOperationsCallback<ArrayList<V>>() {
            public ArrayList<V> doOperations() {
                HashOperations<String, String, Object> ops = RedisSessionStore.this.redisTemplate.opsForHash();
                ArrayList<V> values = (ArrayList)ops.multiGet(sessionId, hashKeys);
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ HMGET {}.{} -> {}", new Object[]{sessionId, hashKeys, values});
                }

                return values;
            }
        });
    }

    public void remove(final String sessionId, final String key) {
        this.execute(new RedisSessionStore.HashOperationsCallback<Serializable>() {
            public Serializable doOperations() {
                HashOperations<String, String, Object> ops = RedisSessionStore.this.redisTemplate.opsForHash();
                ops.delete(sessionId, new Object[]{key});
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ HDEL {}.{}", new Object[]{sessionId, key});
                }

                return null;
            }
        });
    }

    public <V extends Serializable> void set(final String sessionId, final String key, final V value) {
        this.execute(new RedisSessionStore.HashOperationsCallback<V>() {
            public V doOperations() {
                HashOperations ops;
                if (value == null) {
                    ops = RedisSessionStore.this.redisTemplate.opsForHash();
                    ops.delete(sessionId, new Object[]{key});
                    if (RedisSessionStore.logger.isDebugEnabled()) {
                        RedisSessionStore.logger.debug("$$$ HDEL {}.{}", new Object[]{sessionId, key});
                    }
                } else {
                    ops = RedisSessionStore.this.redisTemplate.opsForHash();
                    ops.put(sessionId, key, value);
                    if (RedisSessionStore.logger.isDebugEnabled()) {
                        RedisSessionStore.logger.debug("$$$ HSET {}.{} -> {}", new Object[]{sessionId, key, value});
                    }
                }

                return null;
            }
        });
    }

    public <V extends Serializable> void set(final String sessionId, final Map<String, V> m) {
        this.execute(new RedisSessionStore.HashOperationsCallback<V>() {
            public V doOperations() {
                HashOperations<String, String, Object> ops = RedisSessionStore.this.redisTemplate.opsForHash();
                ops.putAll(sessionId, m);
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ HMSET {} -> {}", new Object[]{sessionId, m});
                }

                return null;
            }
        });
    }

    public void setExpire(final String sessionId, final int expireInSeconds) {
        this.execute(new RedisSessionStore.HashOperationsCallback<Serializable>() {
            public Serializable doOperations() {
                RedisSessionStore.this.redisTemplate.expire(sessionId, (long)expireInSeconds, TimeUnit.SECONDS);
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ EXPIRE {} {} SECONDS", new Object[]{sessionId, expireInSeconds});
                }

                return null;
            }
        });
    }

    public LinkedHashSet<String> getKeys(final String sessionId) {
        return (LinkedHashSet)this.execute(new RedisSessionStore.HashOperationsCallback<LinkedHashSet<String>>() {
            public LinkedHashSet<String> doOperations() {
                HashOperations<String, String, Object> ops = RedisSessionStore.this.redisTemplate.opsForHash();
                LinkedHashSet<String> set = (LinkedHashSet)ops.keys(sessionId);
                if (RedisSessionStore.logger.isDebugEnabled()) {
                    RedisSessionStore.logger.debug("$$$ HKEYS {} -> {}", new Object[]{sessionId, set});
                }

                return set;
            }
        });
    }

    private <V extends Serializable> V execute(RedisSessionStore.HashOperationsCallback<V> callback) {
        int curRetryTime = 0;
        boolean hasConnectionTimeoutException = true;

        while(curRetryTime < 3 && hasConnectionTimeoutException) {
            hasConnectionTimeoutException = false;

            try {
                return (V)callback.doOperations();
            } catch (InvalidDataAccessApiUsageException var5) {
                if (var5.getMessage().contains("ERR Connection timed out")) {
                    ++curRetryTime;
                    logger.warn("retry " + curRetryTime + " times, cause " + var5.getMessage(), var5);
                    hasConnectionTimeoutException = true;
                } else {
                    logger.error(var5.getMessage(), var5);
                }
            } catch (RedisConnectionFailureException var6) {
                if (var6.getMessage().contains("Read timed out")) {
                    ++curRetryTime;
                    logger.warn("retry " + curRetryTime + " times, cause " + var6.getMessage(), var6);
                    hasConnectionTimeoutException = true;
                } else {
                    logger.error(var6.getMessage(), var6);
                }
            } catch (Exception var7) {
                logger.error(var7.getMessage(), var7);
            }
        }

        return null;
    }

    private interface HashOperationsCallback<V> {
        V doOperations();
    }
}
