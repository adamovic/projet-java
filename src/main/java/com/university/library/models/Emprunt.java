package com.university.library.models;

import java.util.Date;
import java.util.Calendar;

public class Emprunt {
    private String id;
    private Etudiant etudiant;
    private Livre livre;
    private Date dateEmprunt;
    private Date dateRetourPrevue;
    private Date dateRetour;
    private static final int DUREE_EMPRUNT_JOURS = 14;

    public Emprunt(String id, Etudiant etudiant, Livre livre) {
        this.id = id;
        this.etudiant = etudiant;
        this.livre = livre;
        this.dateEmprunt = new Date();
        this.dateRetourPrevue = calculerDateRetourPrevue();
        this.dateRetour = null;
    }
    
    private Date calculerDateRetourPrevue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateEmprunt);
        cal.add(Calendar.DAY_OF_MONTH, DUREE_EMPRUNT_JOURS);
        return cal.getTime();
    }

    // Getters
    public String getId() {
        return id;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public Livre getLivre() {
        return livre;
    }

    public Date getDateEmprunt() {
        return dateEmprunt;
    }

    public Date getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public Date getDateRetour() {
        return dateRetour;
    }

    // Setters
    public void setDateRetour(Date dateRetour) {
        this.dateRetour = dateRetour;
    }
    
    public void setDateEmprunt(Date dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }
    
    public void setDateRetourPrevue(Date dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    // Méthodes métier
    public boolean estActif() {
        return dateRetour == null;
    }

    public boolean estEnRetard() {
        if (!estActif()) {
            return false;
        }
        return new Date().after(dateRetourPrevue);
    }

    public int getNombreJoursRetard() {
        if (!estEnRetard()) {
            return 0;
        }
        long diff = new Date().getTime() - dateRetourPrevue.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public int getNombreJoursRestants() {
        if (!estActif()) {
            return 0;
        }
        if (estEnRetard()) {
            return 0;
        }
        long diff = dateRetourPrevue.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean retourner() {
        if (estActif()) {
            this.dateRetour = new Date();
            return true;
        }
        return false;
    }

    public String getStatut() {
        if (!estActif()) {
            return "Retourné";
        } else if (estEnRetard()) {
            return "En retard (" + getNombreJoursRetard() + " jours)";
        } else {
            return "En cours (" + getNombreJoursRestants() + " jours restants)";
        }
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "id='" + id + '\'' +
                ", etudiant=" + etudiant.getNomComplet() +
                ", livre=" + livre.getTitre() +
                ", statut=" + getStatut() +
                '}';
    }
}
