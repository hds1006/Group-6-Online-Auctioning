/**
 * This file contains the UserInterests class, which represents the interests of a user in the system.
 * The UserInterests class is mapped to the "user_interests" table in the database.
 * It provides properties to store the user's interests in various categories such as home, auto, clothing,
 * sports, art, and cosmetics.
 *
 * @author Jayden Williams
 * @version 1.0
 * @since 10-31-2024
 */
package edu.sru.cpsc.webshopping.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents the interests of a user in the system.
 * This class is mapped to the "user_interests" table in the database.
 */
@Entity
@Table(name = "user_interests")
public class UserInterests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User user;
    
    @Column(name = "home")
    private Boolean home;
    
    @Column(name = "auto")
    private Boolean auto;
    
    @Column(name = "clothing")
    private Boolean clothing;
    
    @Column(name = "sports")
    private Boolean sports;
    
    @Column(name = "art")
    private Boolean art;
    
    @Column(name = "cosmetics")
    private Boolean cosmetics;

    /**
     * Gets the ID of the user interests.
     *
     * @return the ID of the user interests
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the user interests.
     *
     * @param id the ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the home interest of the user.
     *
     * @return true if the user is interested in home, false otherwise
     */
    public Boolean getHome() {
        return home;
    }

    /**
     * Sets the home interest of the user.
     *
     * @param home true if the user is interested in home, false otherwise
     */
    public void setHome(Boolean home) {
        this.home = home;
    }

    /**
     * Gets the auto interest of the user.
     *
     * @return true if the user is interested in auto, false otherwise
     */
    public Boolean getAuto() {
        return auto;
    }

    /**
     * Sets the auto interest of the user.
     *
     * @param auto true if the user is interested in auto, false otherwise
     */
    public void setAuto(Boolean auto) {
        this.auto = auto;
    }

    /**
     * Gets the clothing interest of the user.
     *
     * @return true if the user is interested in clothing, false otherwise
     */
    public Boolean getClothing() {
        return clothing;
    }

    /**
     * Sets the clothing interest of the user.
     *
     * @param clothing true if the user is interested in clothing, false otherwise
     */
    public void setClothing(Boolean clothing) {
        this.clothing = clothing;
    }

    /**
     * Gets the sports interest of the user.
     *
     * @return true if the user is interested in sports, false otherwise
     */
    public Boolean getSports() {
        return sports;
    }

    /**
     * Sets the sports interest of the user.
     *
     * @param sports true if the user is interested in sports, false otherwise
     */
    public void setSports(Boolean sports) {
        this.sports = sports;
    }

    /**
     * Gets the art interest of the user.
     *
     * @return true if the user is interested in art, false otherwise
     */
    public Boolean getArt() {
        return art;
    }

    /**
     * Sets the art interest of the user.
     *
     * @param art true if the user is interested in art, false otherwise
     */
    public void setArt(Boolean art) {
        this.art = art;
    }

    /**
     * Gets the cosmetics interest of the user.
     *
     * @return true if the user is interested in cosmetics, false otherwise
     */
    public Boolean getCosmetics() {
        return cosmetics;
    }

    /**
     * Sets the cosmetics interest of the user.
     *
     * @param cosmetics true if the user is interested in cosmetics, false otherwise
     */
    public void setCosmetics(Boolean cosmetics) {
        this.cosmetics = cosmetics;
    }

    /**
     * Gets the user associated with the user interests.
     *
     * @return the user associated with the user interests
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with the user interests.
     *
     * @param user the user to associate with the user interests
     */
    public void setUser(User user) {
        this.user = user;
    }
}