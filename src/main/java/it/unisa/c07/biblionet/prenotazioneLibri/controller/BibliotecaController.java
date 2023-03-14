package it.unisa.c07.biblionet.prenotazioneLibri.controller;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultJwtParser;
import it.unisa.c07.biblionet.model.dao.utente.BibliotecaDAO;
import it.unisa.c07.biblionet.model.entity.Genere;
import it.unisa.c07.biblionet.model.entity.Libro;
import it.unisa.c07.biblionet.model.entity.utente.Biblioteca;
import it.unisa.c07.biblionet.model.form.LibroForm;
import it.unisa.c07.biblionet.prenotazioneLibri.service.PrenotazioneLibriService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Implementa il controller per il sottosistema
 * PrenotazioneLibri, in particolare la gestione
 * delle Biblioteche.
 *
 * @author Viviana Pentangelo, Gianmario Voria
 */
@SessionAttributes("loggedUser")
@Controller
@RequiredArgsConstructor
@RequestMapping("/biblioteca")
public class BibliotecaController {

    /**
     * Il service per effettuare le operazioni di
     * persistenza.
     */
    private final PrenotazioneLibriService prenotazioneService;
    private final BibliotecaDAO bibliotecaDAO;


    private Claims getClaimsFromTokenWithoutKey(String token){
        token = token.substring(7);
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        return  (Claims) jwt.getBody();
    }

    /**
     * Implementa la funzionalità che permette di
     * visualizzare tutte le biblioteche iscritte.
     * @return La view per visualizzare le biblioteche
     */
    @RequestMapping(value = "/visualizza-biblioteche",
            method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public List<Biblioteca> visualizzaListaBiblioteche() {

       return prenotazioneService.getAllBiblioteche();
    }

    /**
     * Implementa la funzionalità che permette di
     * visualizzare la pagina per l'inserimento di
     * nuovi libri prenotabili.
     * @return La view
     */
    /*
    @RequestMapping(value = "/inserisci-nuovo-libro",
                            method = RequestMethod.GET)
    public String visualizzaInserimentoLibro(@RequestHeader (name="Authorization") String token) {

        Claims claims = getClaimsFromTokenWithoutKey(token);
        Biblioteca b =  bibliotecaDAO.getOne(claims.getSubject());

        List<Libro> listaLibri =
                prenotazioneService.visualizzaListaLibriCompleta();
        model.addAttribute("listaLibri", listaLibri);

        List<Genere> listaGeneri = prenotazioneService.getAllGeneri();
        model.addAttribute("listaGeneri", listaGeneri);

        return "/biblioteca/inserimento-nuovo-libro-prenotabile";
    }
    todo suddividere in due metodi diversi, rendendo il tutto più semplice credo
    */

    /**
     * Implementa la funzionalità che permette inserire
     * un libro tramite l'isbn ed una Api di Google.
     * @param isbn l'isbn del libro
     * @param generi la lista dei generi del libro
     * @param numCopie il numero di copie possedute
     * @param token Il token per identificare l'utente
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-isbn",
                        method = RequestMethod.POST)
    public boolean inserisciPerIsbn(@RequestHeader (name="Authorization") String token,
                                   @RequestParam final String isbn,
                                   @RequestParam final String[] generi,
                                   @RequestParam final int numCopie) {

        if (isbn == null) {
            return false;
        }

        Claims claims = getClaimsFromTokenWithoutKey(token);

        Biblioteca b =  bibliotecaDAO.getOne(claims.getSubject());
        List<String> glist = Arrays.asList(generi.clone());
        Libro l = prenotazioneService.inserimentoPerIsbn(
                isbn, b.getEmail(), numCopie, glist);
        if (l == null) {
            return false;
        }
        return true;
    }

    /**
     * Implementa la funzionalità che permette inserire
     * un libro alla lista dei possessi preso
     * dal db.
     * @param idLibro l'ID del libro
     * @param numCopie il numero di copie possedute
     * @param token Il model per identificare l'utente
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-archivio",
                        method = RequestMethod.POST)
    public boolean inserisciDaDatabase(@RequestHeader (name="Authorization") String token,
                                   @RequestParam final int idLibro,
                                   @RequestParam final int numCopie) {
        Claims claims = getClaimsFromTokenWithoutKey(token);

        Biblioteca b = bibliotecaDAO.getOne(claims.getSubject());
        Libro l = prenotazioneService.inserimentoDalDatabase(
                idLibro, b.getEmail(), numCopie);
        return true;
        //todo non credo di aver capito cosa faccia
    }

    /**
     * Implementa la funzionalità che permette inserire
     * un libro manualmente tramite form.
     * @param libro Il libro da salvare
     * @param numCopie il numero di copie possedute
     * @param annoPubblicazione l'anno di pubblicazione
     * @return La view per visualizzare il libro
     */
    @RequestMapping(value = "/inserimento-manuale",
                        method = RequestMethod.POST)
    public boolean inserisciManualmente(@RequestHeader (name="Authorization") String token,
                                       final LibroForm libro,
                                       final int numCopie,
                                       final String annoPubblicazione) {

        Claims claims = getClaimsFromTokenWithoutKey(token);
        Biblioteca b = bibliotecaDAO.getOne(claims.getSubject());
        Libro l = new Libro();
        l.setTitolo(libro.getTitolo());
        if (libro.getIsbn() != null) {
            l.setIsbn(libro.getIsbn());
        }
        if (libro.getDescrizione() != null) {
            l.setDescrizione(libro.getDescrizione());
        }
        l.setCasaEditrice(libro.getCasaEditrice());
        l.setAutore(libro.getAutore());
        if (libro.getImmagineLibro() != null) {
            try {
                byte[] imageBytes = libro.getImmagineLibro().getBytes();
                String base64Image =
                        Base64.getEncoder().encodeToString(imageBytes);
                l.setImmagineLibro(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LocalDateTime anno = LocalDateTime.of(
                Integer.parseInt(annoPubblicazione), 1, 1, 1, 1);
        l.setAnnoDiPubblicazione(anno);
        Libro newLibro = prenotazioneService.inserimentoManuale(
                l, b.getEmail(), numCopie, libro.getGeneri());
        return true;
    }

    /**
     * Implementa la funzionalità che permette di
     * visualizzare le biblioteche filtrate.
     *
     * @param stringa La stringa di ricerca
     * @param filtro  L'informazione su cui filtrare
     * @return La view che visualizza la lista
     */
    @RequestMapping(value = "/ricerca", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public List<Biblioteca> visualizzaListaFiltrata(
            @RequestParam("stringa") final String stringa,
            @RequestParam("filtro") final String filtro){

        switch (filtro) {
            case "nome":
                return  prenotazioneService.getBibliotecheByNome(stringa);
            case "citta":
                return prenotazioneService.getBibliotecheByCitta(stringa);
            default:
               return  prenotazioneService.getAllBiblioteche();

        }

    }

    /**
     * Implementa la funzionalitá di visualizzazione
     * del profilo di una singola biblioteca.
     * @param email della biblioteca
     * @return La view di visualizzazione singola biblioteca
     */
    @RequestMapping(value = "/{email}",
            method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
    public Biblioteca visualizzaDatiBiblioteca(final @PathVariable String email) {
        return prenotazioneService.getBibliotecaById(email);

    }
}
