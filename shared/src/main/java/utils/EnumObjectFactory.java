package utils;

import java.util.EnumMap;

public abstract class EnumObjectFactory<K extends Enum<K>, V> {
    private boolean mapGenerated = false;
    private final EnumMap<K, V> preMadeObjects = new EnumMap<>(getKeyClass());

    protected EnumObjectFactory(boolean preGenerate) {
        if (preGenerate)
            generateValues();
    }

    protected void generateValues() {
        if (mapGenerated) return;
        for (K key : getKeyClass().getEnumConstants()) {
            preMadeObjects.put(key, generateValue(key));
        }
        mapGenerated = true;
    }

    public V get(K key) {
        return preMadeObjects.get(key);
    }

    protected abstract Class<K> getKeyClass();
    protected abstract V generateValue(K key);
}
