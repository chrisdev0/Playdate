package utils.sorters;

import model.User;

import java.util.Comparator;

public class SearchResultSorter implements Comparator<String> {

    private final String searchTerm;

    public SearchResultSorter(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public int compare(String o1, String o2) {
        if (o1.startsWith(searchTerm) && o2.startsWith(searchTerm)) {
            return 0;
        } else if (o1.startsWith(searchTerm)) {
            return -1;
        } else if (o2.startsWith(searchTerm)) {
            return 1;
        } else {
            return 0;
        }
    }

    public int compare(User o1, User o2) {
        return compare(o1.getName(), o2.getName());
    }

}
