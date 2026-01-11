package com.university.library.models;

import java.util.ArrayList;
import java.util.List;

public class Livre {
    private String isbn;
    private String titre;
    private String auteur;
    private String categorie;
    private int anneePublication;
    private int nombreExemplairesTotal;
    private int nombreExemplairesDisponibles;
    private List<Emprunt> emprunts;

    public Livre(String isbn, String titre, String auteur, String categorie, int anneePublication, int nombreExemplairesTotal) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.anneePublication = anneePublication;
        this.nombreExemplairesTotal = nombreExemplairesTotal;
        this.nombreExemplairesDisponibles = nombreExemplairesTotal;
        this.emprunts = new ArrayList<>();
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getCategorie() {
        return categorie;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public int getNombreExemplairesTotal() {
        return nombreExemplairesTotal;
    }

    public int getNombreExemplairesDisponibles() {
        return nombreExemplairesDisponibles;
    }

    public List<Emprunt> getEmprunts() {
        return emprunts;
    }

    // Setters
    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public void setNombreExemplairesTotal(int nombreExemplairesTotal) {
        this.nombreExemplairesTotal = nombreExemplairesTotal;
    }

    // Méthodes métier
    public boolean estDisponible() {
        return nombreExemplairesDisponibles > 0;
    }

    public boolean emprunterExemplaire() {
        if (estDisponible()) {
            nombreExemplairesDisponibles--;
            return true;
        }
        return false;
    }

    public boolean retournerExemplaire() {
        if (nombreExemplairesDisponibles < nombreExemplairesTotal) {
            nombreExemplairesDisponibles++;
            return true;
        }
        return false;
    }

    public void ajouterEmprunt(Emprunt emprunt) {
        emprunts.add(emprunt);
    }

    public int getNombreEmpruntsActifs() {
        return (int) emprunts.stream()
                .filter(e -> e.getDateRetour() == null)
                .count();
    }

    public double getTauxDisponibilite() {
        return (double) nombreExemplairesDisponibles / nombreExemplairesTotal * 100;
    }

    @Override
    public String toString() {
        return "Livre{" +
                "isbn='" + isbn + '\'' +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", disponibles=" + nombreExemplairesDisponibles +
                "/" + nombreExemplairesTotal +
                '}';
    }
}
