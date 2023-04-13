package unibo.common;

public interface IRoomModel {
    // rappresenta la stanza come una matrice di Box

    int getDimX();
    int getDimY();
    void put(int x, int y, IBox box);
}
