package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private int callsMade = 0;
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String lowerBreed = breed.toLowerCase();

        // If already cached, return a copy of cached data
        if (cache.containsKey(lowerBreed)) {
            return new ArrayList<>(cache.get(lowerBreed));
        }

        // Not cached yet → call wrapped fetcher and increment counter
        List<String> subBreeds = fetcher.getSubBreeds(lowerBreed);
        callsMade++;

        // Store a copy in cache
        cache.put(lowerBreed, new ArrayList<>(subBreeds));

        // Return a copy to avoid external modification
        return new ArrayList<>(subBreeds);
    }

    public int getCallsMade() {
        return callsMade;
    }
}