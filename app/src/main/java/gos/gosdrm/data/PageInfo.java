package gos.gosdrm.data;

import java.util.ArrayList;

/**
 * Created by wuxy on 2017/9/25.
 */

public class PageInfo<T> {
    private int count;
    private int currentPage;
    private int nextPage;
    private int pageCount;
    private int pageSize;
    private int previousPage;
    private String type ;
    private ArrayList<T> items;

    public PageInfo() {
    }

    public PageInfo(int count, int currentPage, int nextPage, int pageCount, int pageSize, int previousPage) {
        this.count = count;
        this.currentPage = currentPage;
        this.nextPage = nextPage;
        this.pageCount = pageCount;
        this.pageSize = pageSize;
        this.previousPage = previousPage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public void setPreviousPage(int previousPage) {
        this.previousPage = previousPage;
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
