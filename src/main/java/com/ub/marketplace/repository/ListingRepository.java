package com.ub.marketplace.repository;

import com.ub.marketplace.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    @Query("SELECT l FROM Listing l WHERE " +
           "(:search IS NULL OR LOWER(l.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:category IS NULL OR l.category.name = :category) AND " +
           "l.status = 'available'")
    List<Listing> searchListings(@Param("search") String search,
                                  @Param("category") String category);

    List<Listing> findByUserId(Long userId);
}