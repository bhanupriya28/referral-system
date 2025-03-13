package com.euron.test.presale.referral;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/referrals")
class ReferralController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public ReferralController(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    @PostMapping
    public ResponseEntity<?> createReferral(@RequestBody ReferralCode referralCode) {
        String addressKey = "user:" + referralCode.getAddress();
        String codeKey = "referral:" + referralCode.getRefCode();

        // Check if code is already used
        if (Boolean.TRUE.equals(redisTemplate.hasKey(codeKey))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Referral code already exists.");
        }

        // Check user referral count
        List<Object> userCodes = redisTemplate.opsForList().range(addressKey, 0, -1);
        if (userCodes != null && userCodes.size() >= 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maximum 10 referral codes allowed per user.");
        }

        // Set defaults and timestamps
        if (referralCode.getId() == null) {
            referralCode.setId(UUID.randomUUID().toString());
        }
        if (referralCode.getIsManagerCode() == null) {
            referralCode.setIsManagerCode("0");
        }
        referralCode.setCreatedAt(LocalDateTime.now().toString());
        referralCode.setUpdatedAt(LocalDateTime.now().toString());

        // Save referral code
        redisTemplate.opsForList().rightPush(addressKey, referralCode);
        redisTemplate.opsForValue().set(codeKey, referralCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(referralCode);
    }
    
    
    @PostMapping("/manager")
    public ResponseEntity<?> createManagerReferral(@RequestBody ReferralCode referralCode) {
        String addressKey = "user:" + referralCode.getAddress();
        String codeKey = "referral:" + referralCode.getRefCode();

        // Check if code is already used
        if (Boolean.TRUE.equals(redisTemplate.hasKey(codeKey))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Referral code already exists.");
        }

        // Check user referral count
        List<Object> userCodes = redisTemplate.opsForList().range(addressKey, 0, -1);
        if (userCodes != null && userCodes.size() >= 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maximum 10 referral codes allowed per user.");
        }

        // Set defaults and timestamps
        if (referralCode.getId() == null) {
            referralCode.setId(UUID.randomUUID().toString());
        }
        if (referralCode.getIsManagerCode() == null) {
            referralCode.setIsManagerCode("1");
        }
        referralCode.setCreatedAt(LocalDateTime.now().toString());
        referralCode.setUpdatedAt(LocalDateTime.now().toString());

        // Save referral code
        redisTemplate.opsForList().rightPush(addressKey, referralCode);
        redisTemplate.opsForValue().set(codeKey, referralCode);

        return ResponseEntity.status(HttpStatus.CREATED).body(referralCode);
    }

    @PostMapping("/redeem/{refCode}")
    public ResponseEntity<?> redeemReferral(@PathVariable String refCode) {
        String codeKey = "referral:" + refCode;

        ReferralCode referralCode = (ReferralCode) redisTemplate.opsForValue().get(codeKey);
        if (referralCode == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Referral code not found or already redeemed.");
        }

        // Mark as redeemed by deleting the key
        redisTemplate.delete(codeKey);

        return ResponseEntity.ok("Referral code redeemed successfully.");
    }
    
    @GetMapping
    public ResponseEntity<List<ReferralCode>> getAllActiveReferrals() {
        Set<String> keys = redisTemplate.keys("referral:*");
        List<ReferralCode> activeReferrals = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                Object data = redisTemplate.opsForValue().get(key);
                ReferralCode referralCode = objectMapper.convertValue(data, ReferralCode.class);
                if (referralCode != null) {
                    activeReferrals.add(referralCode);
                }
            }
        }

        return ResponseEntity.ok(activeReferrals);
    }

    @GetMapping("/address/{address}")
    public ResponseEntity<List<ReferralCode>> getReferralsByAddress(@PathVariable String address) {
        String addressKey = "user:" + address;
        List<Object> userCodes = redisTemplate.opsForList().range(addressKey, 0, -1);

        List<ReferralCode> referrals = userCodes != null ?
                userCodes.stream().map(code -> objectMapper.convertValue(code, ReferralCode.class)).collect(Collectors.toList()) :
                Collections.emptyList();

        return ResponseEntity.ok(referrals);
    }
}
