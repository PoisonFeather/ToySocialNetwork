package util.paging;

/**
 * Represents a single page of element along with the total count of elemetns across all pages.
 * @param <E> the type of elements held in this page
 */

public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Constructs a new {@code Page}
     * @param elementsOnPage the elements containted in the current page
     * @param totalNumberOfElements the total number of elements available in storage
     *
     */

    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements){
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * Returns the elements on the current page:
     * @return an {@link Iterable} of elements on this page
     */
    public Iterable<E> getElementsOnPage(){
        return elementsOnPage;
    }

    /**
     * Returns the total number of elements across all pages
     * @return the total count
     */
    public int getTotalNumberOfElements(){
        return totalNumberOfElements;
    }
}
