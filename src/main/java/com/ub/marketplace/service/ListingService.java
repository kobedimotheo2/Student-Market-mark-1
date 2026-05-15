package com.ub.marketplace.service;

import com.ub.marketplace.model.Listing;
import com.ub.marketplace.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<Listing> getAllListings() {
        return listingRepository.searchListings(null, null);
    }

    public List<Listing> searchListings(String search, String category) {
        String cleanSearch = (search == null || search.isBlank()) ? null : search;
        String cleanCategory = (category == null || category.isBlank()) ? null : category;
        return listingRepository.searchListings(cleanSearch, cleanCategory);
    }

    public List<Listing> getListingsByUser(Long userId) {
        return listingRepository.findByUserId(userId);
    }

    public Listing getListingById(Long id) {
        return listingRepository.findById(id).orElse(null);
    }

    public Listing createListing(Listing listing) {
        return listingRepository.save(listing);
    }

    public void deleteListing(Long id) {
        listingRepository.deleteById(id);
    }
}