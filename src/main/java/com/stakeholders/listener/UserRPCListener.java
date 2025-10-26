package com.stakeholders.listener;

import com.stakeholders.config.RabbitMQConfig;
import com.stakeholders.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RPC Server Listener - prima RPC zahteve od Tours servisa
 * 
 * PRIMER: Tours servis šalje poruku "ratkovac" sa routing key "check.user.exists"
 * → Ovaj listener prima poruku, proverava korisnika, vraća odgovor
 */
@Component
public class UserRPCListener {

    private final UserService userService;

    @Autowired
    public UserRPCListener(UserService userService) {
        this.userService = userService;
    }

    /**
     * RPC Handler: Provera da li korisnik postoji
     * 
     * Primjer poruke od Tours servisa: "ratkovac"
     * Vraća: "true" ili "false"
     */
    @RabbitListener(queues = RabbitMQConfig.RPC_CHECK_USER_EXISTS_QUEUE)
    public String handleCheckUserExists(String username) {
        System.out.println("🔄 RPC: Proveravam da li korisnik '" + username + "' postoji...");
        
        try {
            boolean exists = userService.getUserByUsername(username).isPresent();
            System.out.println("✅ RPC odgovor: Korisnik '" + username + "' " + (exists ? "POSTOJI" : "NE POSTOJI"));
            return String.valueOf(exists);
        } catch (Exception e) {
            System.err.println("❌ RPC greška: " + e.getMessage());
            return "false";
        }
    }

    /**
     * RPC Handler: Provera da li je korisnik blokiran
     * 
     * Primjer poruke od Tours servisa: "ratkovac"
     * Vraća: "true" ako je blokiran, "false" ako nije
     */
    @RabbitListener(queues = RabbitMQConfig.RPC_CHECK_USER_BLOCKED_QUEUE)
    public String handleCheckUserBlocked(String username) {
        System.out.println("🔄 RPC: Proveravam da li je korisnik '" + username + "' blokiran...");
        
        try {
            boolean blocked = userService.getUserByUsername(username)
                .map(user -> user.isBlocked())
                .orElse(false);
            
            System.out.println("✅ RPC odgovor: Korisnik '" + username + "' je " + (blocked ? "BLOKIRAN" : "AKTIVAN"));
            return String.valueOf(blocked);
        } catch (Exception e) {
            System.err.println("❌ RPC greška: " + e.getMessage());
            return "false";
        }
    }

    /**
     * RPC Handler: Dobijanje korisničke uloge
     * 
     * Primjer poruke od Tours servisa: "ratkovac"
     * Vraća: "ROLE_GUIDE", "ROLE_TOURIST", itd.
     */
    @RabbitListener(queues = RabbitMQConfig.RPC_GET_USER_ROLE_QUEUE)
    public String handleGetUserRole(String username) {
        System.out.println("🔄 RPC: Dobijam ulogu korisnika '" + username + "'...");
        
        try {
            String role = userService.getUserByUsername(username)
                .map(user -> user.getRole().toString())
                .orElse(null);
            
            System.out.println("✅ RPC odgovor: Uloga = " + role);
            return role != null ? role : "";
        } catch (Exception e) {
            System.err.println("❌ RPC greška: " + e.getMessage());
            return "";
        }
    }
}


