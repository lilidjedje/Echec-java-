import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

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

    public String symboleUnicode() {
        if (couleur == Couleur.BLANC) {
            switch (type) {
                case ROI: return "♔";
                case DAME: return "♕";
                case TOUR: return "♖";
                case FOU: return "♗";
                case CAVALIER: return "♘";
                case PION: return "♙";
            }
        } else {
            switch (type) {
                case ROI: return "♚";
                case DAME: return "♛";
                case TOUR: return "♜";
                case FOU: return "♝";
                case CAVALIER: return "♞";
                case PION: return "♟";
            }
        }
        return "?";
    }

    @Override
    public String toString() {
        return String.valueOf(couleur.toString().charAt(0)) + type.toString().charAt(0);
    }
}

class Echiquier {
    private Piece[][] plateau;
    private Couleur joueurCourant;
    private boolean partieTerminee;
    private String messageFin = "";

    public Echiquier() {
        plateau = new Piece[8][8];
        joueurCourant = Couleur.BLANC;
        partieTerminee = false;
        initialiser();
    }

    public void initialiser() {
        plateau = new Piece[8][8];
        joueurCourant = Couleur.BLANC;
        partieTerminee = false;
        messageFin = "";

        for (int i = 0; i < 8; i++) {
            plateau[1][i] = new Piece(TypePiece.PION, Couleur.NOIR);
            plateau[6][i] = new Piece(TypePiece.PION, Couleur.BLANC);
        }

        plateau[0][0] = new Piece(TypePiece.TOUR, Couleur.NOIR);
        plateau[0][7] = new Piece(TypePiece.TOUR, Couleur.NOIR);
        plateau[7][0] = new Piece(TypePiece.TOUR, Couleur.BLANC);
        plateau[7][7] = new Piece(TypePiece.TOUR, Couleur.BLANC);

        plateau[0][1] = new Piece(TypePiece.CAVALIER, Couleur.NOIR);
        plateau[0][6] = new Piece(TypePiece.CAVALIER, Couleur.NOIR);
        plateau[7][1] = new Piece(TypePiece.CAVALIER, Couleur.BLANC);
        plateau[7][6] = new Piece(TypePiece.CAVALIER, Couleur.BLANC);

        plateau[0][2] = new Piece(TypePiece.FOU, Couleur.NOIR);
        plateau[0][5] = new Piece(TypePiece.FOU, Couleur.NOIR);
        plateau[7][2] = new Piece(TypePiece.FOU, Couleur.BLANC);
        plateau[7][5] = new Piece(TypePiece.FOU, Couleur.BLANC);

        plateau[0][3] = new Piece(TypePiece.DAME, Couleur.NOIR);
        plateau[7][3] = new Piece(TypePiece.DAME, Couleur.BLANC);

        plateau[0][4] = new Piece(TypePiece.ROI, Couleur.NOIR);
        plateau[7][4] = new Piece(TypePiece.ROI, Couleur.BLANC);
    }

    public Piece getPiece(int ligne, int colonne) {
        if (!caseValide(ligne, colonne)) {
            return null;
        }
        return plateau[ligne][colonne];
    }

    public Couleur getJoueurCourant() {
        return joueurCourant;
    }

    public boolean estPartieTerminee() {
        return partieTerminee;
    }

    public String getMessageFin() {
        return messageFin;
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

    private boolean deplacementPion(Piece piece, int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        int direction = (piece.couleur == Couleur.BLANC) ? -1 : 1;
        int ligneDepartInitiale = (piece.couleur == Couleur.BLANC) ? 6 : 1;
        Piece destination = plateau[ligneArrivee][colonneArrivee];

        if (colonneDepart == colonneArrivee && ligneArrivee == ligneDepart + direction && destination == null) {
            return true;
        }

        if (colonneDepart == colonneArrivee
                && ligneDepart == ligneDepartInitiale
                && ligneArrivee == ligneDepart + 2 * direction
                && destination == null
                && plateau[ligneDepart + direction][colonneDepart] == null) {
            return true;
        }

        if (Math.abs(colonneArrivee - colonneDepart) == 1
                && ligneArrivee == ligneDepart + direction
                && destination != null
                && destination.couleur != piece.couleur) {
            return true;
        }

        return false;
    }

    private boolean deplacementValide(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        if (!caseValide(ligneDepart, colonneDepart) || !caseValide(ligneArrivee, colonneArrivee)) {
            return false;
        }

        Piece piece = plateau[ligneDepart][colonneDepart];
        if (piece == null) {
            return false;
        }

        if (piece.couleur != joueurCourant) {
            return false;
        }

        if (ligneDepart == ligneArrivee && colonneDepart == colonneArrivee) {
            return false;
        }

        Piece destination = plateau[ligneArrivee][colonneArrivee];
        if (destination != null && destination.couleur == piece.couleur) {
            return false;
        }

        int dl = ligneArrivee - ligneDepart;
        int dc = colonneArrivee - colonneDepart;

        switch (piece.type) {
            case PION:
                return deplacementPion(piece, ligneDepart, colonneDepart, ligneArrivee, colonneArrivee);

            case TOUR:
                return (ligneDepart == ligneArrivee || colonneDepart == colonneArrivee)
                        && cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee);

            case FOU:
                return Math.abs(dl) == Math.abs(dc)
                        && cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee);

            case DAME:
                return ((ligneDepart == ligneArrivee || colonneDepart == colonneArrivee)
                        || (Math.abs(dl) == Math.abs(dc)))
                        && cheminLibre(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee);

            case CAVALIER:
                return (Math.abs(dl) == 2 && Math.abs(dc) == 1) || (Math.abs(dl) == 1 && Math.abs(dc) == 2);

            case ROI:
                return Math.abs(dl) <= 1 && Math.abs(dc) <= 1;

            default:
                return false;
        }
    }

    public boolean deplacer(int ligneDepart, int colonneDepart, int ligneArrivee, int colonneArrivee) {
        if (partieTerminee || !deplacementValide(ligneDepart, colonneDepart, ligneArrivee, colonneArrivee)) {
            return false;
        }

        Piece piece = plateau[ligneDepart][colonneDepart];
        Piece cible = plateau[ligneArrivee][colonneArrivee];

        if (cible != null && cible.type == TypePiece.ROI) {
            plateau[ligneArrivee][colonneArrivee] = piece;
            plateau[ligneDepart][colonneDepart] = null;
            partieTerminee = true;
            messageFin = "Le roi " + (cible.couleur == Couleur.BLANC ? "blanc" : "noir") + " a été capturé. "
                    + (joueurCourant == Couleur.BLANC ? "Les blancs gagnent !" : "Les noirs gagnent !");
            return true;
        }

        plateau[ligneArrivee][colonneArrivee] = piece;
        plateau[ligneDepart][colonneDepart] = null;

        if (piece.type == TypePiece.PION) {
            if ((piece.couleur == Couleur.BLANC && ligneArrivee == 0)
                    || (piece.couleur == Couleur.NOIR && ligneArrivee == 7)) {
                plateau[ligneArrivee][colonneArrivee] = new Piece(TypePiece.DAME, piece.couleur);
            }
        }

        joueurCourant = (joueurCourant == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
        return true;
    }
}

public class EchiquierSwing extends JFrame {
    private final Echiquier echiquier;
    private final JButton[][] cases;
    private final JLabel statutLabel;
    private final JLabel selectionLabel;

    private int ligneSelectionnee = -1;
    private int colonneSelectionnee = -1;

    public EchiquierSwing() {
        echiquier = new Echiquier();
        cases = new JButton[8][8];

        setTitle("Jeu d'échecs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel haut = new JPanel(new GridLayout(2, 1));
        statutLabel = new JLabel("Tour : Blanc", SwingConstants.CENTER);
        statutLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectionLabel = new JLabel("Clique sur une pièce puis sur sa destination.", SwingConstants.CENTER);
        haut.add(statutLabel);
        haut.add(selectionLabel);
        add(haut, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(8, 8));
        Font pieceFont = new Font("SansSerif", Font.PLAIN, 36);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton bouton = new JButton();
                bouton.setFont(pieceFont);
                bouton.setFocusPainted(false);
                bouton.setOpaque(true);
                bouton.setBorder(new LineBorder(Color.BLACK));

                final int ligne = i;
                final int colonne = j;
                bouton.addActionListener(e -> gererClic(ligne, colonne));

                cases[i][j] = bouton;
                grille.add(bouton);
            }
        }

        add(grille, BorderLayout.CENTER);

        JPanel bas = new JPanel();
        JButton resetButton = new JButton("Nouvelle partie");
        resetButton.addActionListener(e -> recommencerPartie());
        bas.add(resetButton);
        add(bas, BorderLayout.SOUTH);

        mettreAJourAffichage();

        setSize(720, 760);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void gererClic(int ligne, int colonne) {
        if (echiquier.estPartieTerminee()) {
            JOptionPane.showMessageDialog(this, echiquier.getMessageFin());
            return;
        }

        Piece pieceCliquee = echiquier.getPiece(ligne, colonne);

        if (ligneSelectionnee == -1) {
            if (pieceCliquee == null) {
                selectionLabel.setText("Sélectionne d'abord une pièce.");
                return;
            }

            if (pieceCliquee.couleur != echiquier.getJoueurCourant()) {
                selectionLabel.setText("Tu dois jouer une pièce de ton camp.");
                return;
            }

            ligneSelectionnee = ligne;
            colonneSelectionnee = colonne;
            selectionLabel.setText("Pièce sélectionnée : " + nomCase(ligne, colonne));
            mettreAJourAffichage();
            return;
        }

        if (ligneSelectionnee == ligne && colonneSelectionnee == colonne) {
            ligneSelectionnee = -1;
            colonneSelectionnee = -1;
            selectionLabel.setText("Sélection annulée.");
            mettreAJourAffichage();
            return;
        }

        boolean succes = echiquier.deplacer(ligneSelectionnee, colonneSelectionnee, ligne, colonne);

        if (succes) {
            ligneSelectionnee = -1;
            colonneSelectionnee = -1;
            if (echiquier.estPartieTerminee()) {
                selectionLabel.setText("Partie terminée.");
                mettreAJourAffichage();
                JOptionPane.showMessageDialog(this, echiquier.getMessageFin());
            } else {
                selectionLabel.setText("Déplacement réussi.");
                mettreAJourAffichage();
            }
        } else {
            selectionLabel.setText("Déplacement impossible.");
            ligneSelectionnee = -1;
            colonneSelectionnee = -1;
            mettreAJourAffichage();
        }
    }

    private void recommencerPartie() {
        echiquier.initialiser();
        ligneSelectionnee = -1;
        colonneSelectionnee = -1;
        selectionLabel.setText("Clique sur une pièce puis sur sa destination.");
        mettreAJourAffichage();
    }

    private void mettreAJourAffichage() {
        Color clair = new Color(240, 217, 181);
        Color fonce = new Color(181, 136, 99);
        Color selection = new Color(246, 246, 105);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton bouton = cases[i][j];
                Piece piece = echiquier.getPiece(i, j);

                if (piece == null) {
                    bouton.setText("");
                } else {
                    bouton.setText(piece.symboleUnicode());
                }

                if (i == ligneSelectionnee && j == colonneSelectionnee) {
                    bouton.setBackground(selection);
                } else {
                    bouton.setBackground((i + j) % 2 == 0 ? clair : fonce);
                }
            }
        }

        String joueur = echiquier.getJoueurCourant() == Couleur.BLANC ? "Blanc" : "Noir";
        statutLabel.setText(echiquier.estPartieTerminee() ? "Partie terminée" : "Tour : " + joueur);
    }

    private String nomCase(int ligne, int colonne) {
        char col = (char) ('A' + colonne);
        int lig = 8 - ligne;
        return "" + col + lig;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EchiquierSwing::new);
    }
}

