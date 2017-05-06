package dbtest;

import dblayer.PaginationWrapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Extra {

    @Test
    public void testSizeOfPagination() {
        List<String> list = new ArrayList<>();
        list.add("test");
        PaginationWrapper<String> paginationWrapper = new PaginationWrapper<>(list, 0);
        assertEquals(1, paginationWrapper.size());
    }

}
