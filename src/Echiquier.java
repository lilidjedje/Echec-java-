import java.util.Scanner;

enum Couleur {
    BLANC, NOIR
}

enum TypePiece {
    ROI, DAME, TOUR, FOU, CAVALIER, PION
}

class Piece {
    TypePiece type;
    Couleur couleur;

    public Piece(TypePiece type, Couleur couleur) {
        this.type = type;
        this.couleur = couleur;
    }

    @Override
    public String toString() {
        return String.valueOf(couleur.toString().charAt(0))
                + type.toString().charAt(0);
    }
}

public class Echiquier {
    private Piece[][] plateau;
    private Couleur joueurCourant;
    private boolean partieTerminee;

    public Echiquier() {
        plateau = new Piece[8][8];
        joueurCourant = Couleur.BLANC;
        partieTerminee = false;
        initialiser();
    }

    private void initialiser() {
        // Pions
        for (int i = 0; i < 8; i++) {
            plateau[1][i] = new Piece(TypePiece.PION, Couleur.NOIR);
            plateau[6][i] = new Piece(TypePiece.PION, Couleur.BLANC);
        }

        // Tours
        plateau[0][0] = new Piece(TypePiece.TOUR, Couleur.NOIR);
        plateau[0][7] = new Piece(TypePiece.TOUR, Couleur.NOIR);
        plateau[7][0] = new Piece(TypePiece.TOUR, Couleur.BLANC);
        plateau[7][7] = new Piece(TypePiece.TOUR, Couleur.BLANC);

        // Cavaliers
        plateau[0][1] = new Piece(TypePiece.CAVALIER, Couleur.NOIR);
        plateau[0][6] = new Piece(TypePiece.CAVALIER, Couleur.NOIR);
        plateau[7][1] = new Piece(TypePiece.CAVALIER, Couleur.BLANC);
        plateau[7][6] = new Piece(TypePiece.CAVALIER, Couleur.BLANC);

        // Fous
        plateau[0][2] = new Piece(TypePiece.FOU, Couleur.NOIR);
        plateau[0][5] = new Piece(TypePiece.FOU, Couleur.NOIR);
        plateau[7][2] = new Piece(TypePiece.FOU, Couleur.BLANC);
        plateau[7][5] = new Piece(TypePiece.FOU, Couleur.BLANC);

        // Dames
        plateau[0][3] = new Piece(TypePiece.DAME, Couleur.NOIR);
        plateau[7][3] = new Piece(TypePiece.DAME, Couleur.BLANC);

        // Rois
        plateau[0][4] = new Piece(TypePiece.ROI, Couleur.NOIR);
        plateau[7][4] = new Piece(TypePiece.ROI, Couleur.BLANC);
    }

    public void afficher() {
        System.out.println();
        System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.println();

        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "  ");
            for (int j = 0; j < 8; j++) {
                if (plateau[i][j] == null) {
                    System.out.print(" . ");
                } else {
                    System.out.print(plateau[i][j] + " ");
                }
            }
            System.out.println(" " + (8 - i));
        }

        System.out.print("   ");
        for (char c = 'A'; c <= 'H'; c++) {
            System.out.print(" " + c + " ");
        }
        System.out.println("\n");
    }

    public void afficherJoueurCourant() {
        System.out.println("C'est au tour des " +
                (joueurCourant == Couleur.BLANC ? "blancs" : "noirs") + ".");
    }

    private boolean caseValide(int ligne, int colonne) {
        return ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8;
    }

    private boolean cheminLibre(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        int deltaLigne = Integer.compare(ligneArrivee, ligneDepart);
        int deltaColonne = Integer.compare(colonneArrivee, colonneDepart);

        int l = ligneDepart + deltaLigne;
        int c = colonneDepart + deltaColonne;

        while (l != ligneArrivee || c != colonneArrivee) {
            if (plateau[l][c] != null) {
                return false;
            }
            l += deltaLigne;
            c += deltaColonne;
        }

        return true;
    }

    private boolean deplacementValide(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        if (!caseValide(ligneDepart, colonneDepart) || !caseValide(ligneArrivee, colonneArrivee)) {
            System.out.println("Case invalide.");
            return false;
        }

        Piece piece = plateau[ligneDepart][colonneDepart];

        if (piece == null) {
            System.out.println("Aucune pièce sur la case de départ.");
            return false;
        }

        if (piece.couleur != joueurCourant) {
            System.out.println("Ce n'est pas ton tour.");
            return false;
        }

        if (ligneDepart == ligneArrivee && colonneDepart == colonneArrivee) {
            System.out.println("La pièce doit bouger.");
            return false;
        }

        Piece destination = plateau[ligneArrivee][colonneArrivee];
        if (destination != null && destination.couleur == piece.couleur) {
            System.out.println("Tu ne peux pas capturer ta propre pièce.");
            return false;
        }

        int dl = ligneArrivee - ligneDepart;
        int dc = colonneArrivee - colonneDepart;

        switch (piece.type) {
            case PION:
                return deplacementPion(piece, ligneDepart, colonneDepart, ligneArrivee, colonneArrivee);

            case TOUR:
                if (ligneDepart == ligneArrivee || colonneDepart == colonneArrivee) {
                    if (cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee)) {
                        return true;
                    }
                }
                System.out.println("Déplacement de tour invalide.");
                return false;

            case FOU:
                if (Math.abs(dl) == Math.abs(dc)) {
                    if (cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee)) {
                        return true;
                    }
                }
                System.out.println("Déplacement de fou invalide.");
                return false;

            case DAME:
                if ((ligneDepart == ligneArrivee || colonneDepart == colonneArrivee)
                        || (Math.abs(dl) == Math.abs(dc))) {
                    if (cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee)) {
                        return true;
                    }
                }
                System.out.println("Déplacement de dame invalide.");
                return false;

            case CAVALIER:
                if ((Math.abs(dl) == 2 && Math.abs(dc) == 1) || (Math.abs(dl) == 1 && Math.abs(dc) == 2)) {
                    return true;
                }
                System.out.println("Déplacement de cavalier invalide.");
                return false;

            case ROI:
                if (Math.abs(dl) <= 1 && Math.abs(dc) <= 1) {
                    return true;
                }
                System.out.println("Déplacement de roi invalide.");
                return false;

            default:
                return false;
        }
    }

    private boolean deplacementPion(Piece piece, int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        int direction = (piece.couleur == Couleur.BLANC) ? -1 : 1;
        int ligneDepartInitiale = (piece.couleur == Couleur.BLANC) ? 6 : 1;

        Piece destination = plateau[ligneArrivee][colonneArrivee];

        // Avancer d'une case
        if (colonneDepart == colonneArrivee && ligneArrivee == ligneDepart + direction && destination == null) {
            return true;
        }

        // Avancer de deux cases au premier coup
        if (colonneDepart == colonneArrivee
                && ligneDepart == ligneDepartInitiale
                && ligneArrivee == ligneDepart + 2 * direction
                && destination == null
                && plateau[ligneDepart + direction][colonneDepart] == null) {
            return true;
        }

        // Capture en diagonale
        if (Math.abs(colonneArrivee - colonneDepart) == 1
                && ligneArrivee == ligneDepart + direction
                && destination != null
                && destination.couleur != piece.couleur) {
            return true;
        }

        System.out.println("Déplacement de pion invalide.");
        return false;
    }

    public boolean deplacer(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        if (!deplacementValide(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee)) {
            return false;
        }

        Piece piece = plateau[ligneDepart][colonneDepart];
        Piece cible = plateau[ligneArrivee][colonneArrivee];

        if (cible != null) {
            System.out.println(piece + " capture " + cible + " !");
            if (cible.type == TypePiece.ROI) {
                plateau[ligneArrivee][colonneArrivee] = piece;
                plateau[ligneDepart][colonneDepart] = null;
                afficher();
                System.out.println("Le roi a été capturé !");
                System.out.println("Les " + (joueurCourant == Couleur.BLANC ? "blancs" : "noirs") + " gagnent !");
                partieTerminee = true;
                return true;
            }
        }

        plateau[ligneArrivee][colonneArrivee] = piece;
        plateau[ligneDepart][colonneDepart] = null;

        // Promotion simple en dame
        if (piece.type == TypePiece.PION) {
            if ((piece.couleur == Couleur.BLANC && ligneArrivee == 0)
                    || (piece.couleur == Couleur.NOIR && ligneArrivee == 7)) {
                plateau[ligneArrivee][colonneArrivee] = new Piece(TypePiece.DAME, piece.couleur);
                System.out.println("Promotion du pion en DAME !");
            }
        }

        changerJoueur();
        return true;
    }

    private void changerJoueur() {
        joueurCourant = (joueurCourant == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
    }

    public boolean estPartieTerminee() {
        return partieTerminee;
    }

    private static int[] convertirPosition(String pos) {
        if (pos == null || pos.length() != 2) {
            return null;
        }

        char colonneChar = Character.toUpperCase(pos.charAt(0));
        char ligneChar = pos.charAt(1);

        if (colonneChar < 'A' || colonneChar > 'H' || ligneChar < '1' || ligneChar > '8') {
            return null;
        }

        int colonne = colonneChar - 'A';
        int ligne = 8 - Character.getNumericValue(ligneChar);

        return new int[]{ligne, colonne};
    }

    public void jouer() {
        Scanner scanner = new Scanner(System.in);

        while (!partieTerminee) {
            afficher();
            afficherJoueurCourant();
            System.out.println("Entre un coup : ");
            System.out.println("Tape 'quit' pour quitter.");

            String saisie = scanner.nextLine().trim();

            if (saisie.equalsIgnoreCase("quit")) {
                System.out.println("Partie arrêtée.");
                break;
            }

            String[] morceaux = saisie.split("\\s+");
            if (morceaux.length != 2) {
                System.out.println("Format invalide. Exemple attendu : E2 E4");
                continue;
            }

            int[] depart = convertirPosition(morceaux[0]);
            int[] arrivee = convertirPosition(morceaux[1]);

            if (depart == null || arrivee == null) {
                System.out.println("Coordonnées invalides.");
                continue;
            }

            boolean succes = deplacer(depart[0], depart[1], arrivee[0], arrivee[1]);
            if (!succes) {
                System.out.println("Coup refusé. Réessaie.");
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        Echiquier echiquier = new Echiquier();
        System.out.println("=== Jeu d'échecs Java ===");
        echiquier.jouer();
    }
}