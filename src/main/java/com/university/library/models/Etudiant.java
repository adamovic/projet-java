package com.university.library.models;

import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private List<Emprunt> emprunts;

    public Etudiant(String id, String nom, String prenom, String email, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.emprunts = new ArrayList<>();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public List<Emprunt> getEmprunts() {
        return emprunts;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Méthodes métier
    public String getNomComplet() {
        return nom + " " + prenom;
    }

    public void ajouterEmprunt(Emprunt emprunt) {
        emprunts.add(emprunt);
    }

    public void retirerEmprunt(Emprunt emprunt) {
        emprunts.remove(emprunt);
    }

    public int getNombreEmpruntsActifs() {
        return (int) emprunts.stream()
                .filter(e -> e.getDateRetour() == null)
                .count();
    }

    public boolean aDesEmpruntsEnRetard() {
        return emprunts.stream()
                .anyMatch(e -> e.estEnRetard());
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
