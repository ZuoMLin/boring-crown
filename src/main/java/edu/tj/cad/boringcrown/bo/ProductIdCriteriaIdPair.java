package edu.tj.cad.boringcrown.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
public class ProductIdCriteriaIdPair {

    private int productId;

    private int criteriaId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductIdCriteriaIdPair that = (ProductIdCriteriaIdPair) o;

        if (productId != that.productId) return false;
        return criteriaId == that.criteriaId;
    }

    @Override
    public int hashCode() {
        int result = productId;
        result = 31 * result + criteriaId;
        return result;
    }
}
