package org.boot.cap.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blacklisted_token")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklisted_token_id")
    private Long id;

    @Column(name = "token_value")
    private String tokenValue; // 토큰 값

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BlacklistedToken(String tokenValue) {
        this.tokenValue = tokenValue;
    }

}
