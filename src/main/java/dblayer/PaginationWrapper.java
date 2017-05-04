package dblayer;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

@Data
public class PaginationWrapper<T> {

    private Collection<T> collection;
    private int paginationOffset;

    public PaginationWrapper(Collection<T> collection, int paginationOffset) {
        this.collection = collection;
        this.paginationOffset = paginationOffset;
    }

    public Stream<T> stream() {
        return collection.stream();
    }

    public int size() {
        return collection.size();
    }

    public static class PaginationWrapperForNull<T> extends PaginationWrapper<T> {

        public PaginationWrapperForNull() {
            super(new ArrayList<T>(), -1);
        }
    }
}
