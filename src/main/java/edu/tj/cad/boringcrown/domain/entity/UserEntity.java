package edu.tj.cad.boringcrown.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zuomlin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    private int userId;

    private String username;

    private String password;

    private String role;

    private String permission;

    /**
     * @see UserStatus
     */
    private int status;

    @Override
    public String toString() {
        return username;
    }

    public enum UserStatus {

        JUDGE_OPEN(1000, "评分中"),
        JUDGE_FINISHED(1001, "评分结束"),

        ADMIN_DEFAULT(2000, "默认");

        public int status;

        public String desc;

        UserStatus(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }
    }
}
