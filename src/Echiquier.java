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
        return couleur.toString().charAt(0) + "_" + type.toString().charAt(0);
    }
}

public class Echiquier {
    private Piece[][] plateau;

    public Echiquier() {
        plateau = new Piece[8][8];
        initialiser();
    }

    private void initialiser() {
        // Pions
        for (int i = 0; i < 8; i++) {
            plateau[1][i] = new Piece(TypePiece.PION, Couleur.NOIR);
            plateau[6][i] = new Piece(TypePiece.PION, Couleur.BLANC);
        }

        // Tours
        plateau[0][0] = plateau[0][7] = new Piece(TypePiece.TOUR, Couleur.NOIR);
        plateau[7][0] = plateau[7][7] = new Piece(TypePiece.TOUR, Couleur.BLANC);

        // Cavaliers
        plateau[0][1] = plateau[0][6] = new Piece(TypePiece.CAVALIER, Couleur.NOIR);
        plateau[7][1] = plateau[7][6] = new Piece(TypePiece.CAVALIER, Couleur.BLANC);

        // Fous
        plateau[0][2] = plateau[0][5] = new Piece(TypePiece.FOU, Couleur.NOIR);
        plateau[7][2] = plateau[7][5] = new Piece(TypePiece.FOU, Couleur.BLANC);

        // Dames
        plateau[0][3] = new Piece(TypePiece.DAME, Couleur.NOIR);
        plateau[7][3] = new Piece(TypePiece.DAME, Couleur.BLANC);

        // Rois
        plateau[0][4] = new Piece(TypePiece.ROI, Couleur.NOIR);
        plateau[7][4] = new Piece(TypePiece.ROI, Couleur.BLANC);
    }

    public void afficher() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (plateau[i][j] == null) {
                    System.out.print(" . ");
                } else {
                    System.out.print(plateau[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Echiquier echiquier = new Echiquier();
        echiquier.afficher();
    }
}