package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.Profile;
import org.yearup.service.ProfileService;
import org.yearup.utils.ValidationCheck;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("profile")
public class ProfileController {

    private final ProfileService profileService;


    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> getProfile(Principal principal){
        Profile userProfile = profileService.getProfileById(principal);

        ValidationCheck.userValidation(userProfile);

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> updateProfile(Principal principal, @RequestBody Profile profile){

        Profile updatedProfile = profileService.updateProfile(principal, profile);

        return ResponseEntity.ok(updatedProfile);

    }

    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProfile(Principal principal){
        int userId = profileService.getUserId(principal);
        profileService.deleteProfile(userId);
        return ResponseEntity.ok().build();
    }
}
