package com.brenda.clexis.clientGatewayService.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class DirectCloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public void makeResourcePublic(String publicId) throws IOException {
        // Update the resource with public access mode
        Map<String, Object> result = cloudinary.uploader().explicit(publicId, 
            ObjectUtils.asMap(
                "type", "upload",
                "access_mode", "public"
            )
        );
        
        System.out.println("Resource updated: " + result);
    }
}