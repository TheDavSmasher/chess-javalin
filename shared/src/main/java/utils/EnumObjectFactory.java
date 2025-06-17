package utils;

import java.util.EnumMap;

public abstract class EnumObjectFactory<K extends Enum<K>, V> {
    private final EnumMap<K, V> preMadeObjects = new EnumMap<>(getKeyClass());

    protected EnumObjectFactory() {
        for (K key : getKeyClass().getEnumConstants()) {
            preMadeObjects.put(key, preGenerateValue(key));
        }
    }

    public V get(K key) {
        return preMadeObjects.get(key);
    }

    protected abstract Class<K> getKeyClass();
    protected abstract V preGenerateValue(K key);
}
