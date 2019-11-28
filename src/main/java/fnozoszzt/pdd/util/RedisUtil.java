package fnozoszzt.pdd.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import java.util.Set;

@Repository("redisUtil")
public class RedisUtil {
    public static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private JedisPoolConfig jpc;
    private JedisPool pool;

    @Value("${redis.server.host}")
    private String redisHost;
    @Value("${redis.server.port}")
    private Integer redisPort;
    @Value("${global.session.timeout}")
    private Integer sessionTimeout;

    @PostConstruct
    public void init() {
        jpc = new JedisPoolConfig();
        pool = new JedisPool(jpc, redisHost, redisPort);
    }

    /**
     * 该方法是原子的，如果key不存在，则设置当前key成功，返回1；如果当前key已经存在，则设置当前key失败，返回0。
     * nx 是not exist 的意思
     * @param key
     * @param value
     * @return
     */
    public Long setnx(final String key, final String value) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Long ret = jedis.setnx(key,value);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" setnx key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }

    /**
     * 该方法是原子的，对key设置value这个值，并且返回key原来的旧值
     * @param key
     * @param value
     * @return
     */
    public String getSet(final String key,final String value) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String ret = jedis.getSet(key,value);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" getSet key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }
    public String set(final String key,final String value) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String ret=	jedis.set(key,value);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" set key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }

    public String get(final String key) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String ret = jedis.get(key);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" get key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }
    
    public long delete(final String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            long ret = jedis.del(key);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
    }

    public Set<String> keys(final String pattern){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> ret = jedis.keys(pattern);
            return ret;
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + pattern, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
    }

    /**
     * Redis Sadd 命令将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。
     * 假如集合 key 不存在，则创建一个只包含添加的元素作成员的集合。
     * 当集合 key 不是集合类型时，返回一个错误。
     * @param key
     * @param value
     * @return
     */
    public Long sadd(final String key,final String value) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" sadd key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }

    /**
     * Redis Srem 命令用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略。
     * 当 key 不是集合类型，返回一个错误。
     * @param key
     * @param value
     * @return
     */
    public Long srem(final String key,final String value) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" srem key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }

    /**
     * Redis Smembers 命令返回集合中的所有的成员。 不存在的集合 key 被视为空集合。
     * @param key
     * @return
     */
    public Set<String> smembers(final String key) {
        long time = System.currentTimeMillis();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smembers(key);
        } catch (Exception e) {
            logger.error("redis operation fail! key:" + key, e);
            if (jedis != null) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }
            throw new JedisException(e);
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
            logger.debug(" smembers key:" + key+ " time cost:"
                    + (System.currentTimeMillis() - time));
        }
    }
}
