package it.unisa.c07.biblionet.model.entity.utente;

import it.unisa.c07.biblionet.model.entity.Possesso;
import it.unisa.c07.biblionet.model.entity.TicketPrestito;
import it.unisa.c07.biblionet.utils.Length;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

/**
 * Questa classe rappresenta una Biblioteca.
 * Una Biblioteca possiede un nome, la lista degli esperti
 * che lavorano presso di essa, la lista di libri che possiede
 * che quindi può prestare ad un lettore,
 * e una lista di ticket che rappresentano le richieste di prestito dei lettori.
 */
@Entity
@SuperBuilder
@Data
@EqualsAndHashCode
@NoArgsConstructor
public class Biblioteca {

    /**
     * Rappresenta l'ID di un utente registrato. todo è sostanzialmente una chiave esterna (il resto dei dati è nel microservizio autenticazione)
     */
    @Id
    @Column(nullable = false, length = Length.LENGTH_320)
    @NonNull
    private String email;


    /**
     * Rappresenta il nome della biblioteca.
     */
    @Column(nullable = false, length = Length.LENGTH_60)
    @NonNull
    private String nomeBiblioteca;

    /**
     * Rappresenta la lista di esperti che lavorano nella biblioteca.
     * todo questa relazione va modellata al contrario solo in esperto
     */
    /*
    @OneToMany(mappedBy = "biblioteca")
    @ToString.Exclude
    private List<Esperto> esperti;
    */

    /**
     * Rappresenta la lista di ticket riguardanti le richieste di prestito.
     */
    @OneToMany
    @ToString.Exclude
    private List<TicketPrestito> tickets;

    /**
     * Rappresenta la lista di libri posseduti dalla biblioteca.
     */
    @OneToMany(mappedBy = "possessoID.bibliotecaID")
    @ToString.Exclude
    private List<Possesso> possessi;

    /**
     * Rappresente la provincia dove vive l'utente registrato.
     */
    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String provincia;

    /**
     * Rappresenta la città dove vive l'utente registrato.
     */
    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String citta;

    /**
     * Rappresenta la via dove vive l'utente registrato.
     */
    @Column(nullable = false, length = Length.LENGTH_30)
    @NonNull
    private String via;

    /**
     * Rappresenta il recapito telefonico dell'utente registrato.
     */
    @Column(nullable = false, length = Length.LENGTH_10)
    @NonNull
    private String recapitoTelefonico;


    /**
     *
     * @param email È la mail della biblioteca.
     * @param provincia È la provincia in cui ha sede la biblioteca.
     * @param citta È la città in cui ha sede la biblioteca.
     * @param via È l'indirizzo in cui ha sede la biblioteca.
     * @param recapitoTelefonico È il numero di telefono della biblioteca.
     * @param nomeBiblioteca È il nome della biblioteca.
     */
    public Biblioteca(final String email,
                      final String provincia, final String citta,
                      final String via, final String recapitoTelefonico,
                      final String nomeBiblioteca) {

        this.email = email;
        this.provincia = provincia;
        this.citta = citta;
        this.via = via;
        this.recapitoTelefonico = recapitoTelefonico;
        this.nomeBiblioteca = nomeBiblioteca;
    }

}
