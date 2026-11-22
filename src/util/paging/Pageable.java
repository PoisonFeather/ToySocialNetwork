package util.paging;


/**
 * Encapsulates pagination request parameters : zero-based page index and page size.
 */

public class Pageable{
    private final int pageNumber;
    private final int pageSize;

    /**
     * Constructs a new {@code Pageable} instance
     *
     * @param pageNumber the zero based index (must be {@code>=0})
     * @param pageSize the maximum number of elements per page (must be{@code>0}
     * @throws IllegalArgumentException if {@code pageNumber<0} or {@code pageSize<0}
     */

    public Pageable(int pageNumber, int pageSize){
        if(pageNumber < 0) throw new IllegalArgumentException("Page number must be > 0");
        if(pageSize < 0) throw new IllegalArgumentException("Page size must be > 0");
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Returns the zero-based page index
     * @return the page number
     */
    public int getPageNumber(){
        return pageNumber;
    }

    /**
     * Returns the number of elements per page
     * @return page size
     */
    public int getPageSize(){
        return pageSize;
    }
}