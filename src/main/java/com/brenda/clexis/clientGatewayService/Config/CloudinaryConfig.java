package com.brenda.clexis.clientGatewayService.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dntyjc31a",
            "api_key", "414238561683564",
            "api_secret", "s3dj4UxqGUSRmP3BO2KNaIYkbFo"
        ));
    }
}