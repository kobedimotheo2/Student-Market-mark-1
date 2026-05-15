package com.ub.marketplace.controller;

import com.ub.marketplace.JwtUtil;
import com.ub.marketplace.model.Listing;
import com.ub.marketplace.model.User;
import com.ub.marketplace.repository.UserRepository;
import com.ub.marketplace.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public ListingController(ListingService listingService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.listingService = listingService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Listing> getAllListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return listingService.searchListings(search, category);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyListings(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow();
            return ResponseEntity.ok(listingService.getListingsByUser(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listing> getListingById(@PathVariable Long id) {
        Listing listing = listingService.getListingById(id);
        if (listing == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(listing);
    }

    @PostMapping
    public ResponseEntity<?> createListing(
            @RequestBody Listing listing,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow();
            listing.setUser(user);
            return ResponseEntity.ok(listingService.createListing(listing));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(email).orElseThrow();

            Listing listing = listingService.getListingById(id);
            if (listing == null) return ResponseEntity.notFound().build();
            if (!listing.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You can only delete your own listings");
            }

            listingService.deleteListing(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}