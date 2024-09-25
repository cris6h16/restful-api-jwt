package org.cris6h16.Repositories.Page;

public record MySortOrder(String property, MyDirection direction) {
    public enum MyDirection {
        ASC, DESC
    }
}
