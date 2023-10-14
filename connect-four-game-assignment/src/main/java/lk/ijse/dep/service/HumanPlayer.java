/**
 * create by Nethmina
 * date : 10/1/2023 - 12:29 PM
 */

package lk.ijse.dep.service;

public class HumanPlayer extends Player {
    public HumanPlayer(Board newBoard) {
        super(newBoard);
    }
    @Override
    public void movePiece(int col) {
        if (board.isLegalMove(col)) {
            board.updateMove(col, Piece.BLUE);
            board.getBoardUI().update(col, true);

            Winner win = board.findWinner();

            if (win.getWinningPiece() == Piece.EMPTY) {
                if (!board.existLegalMoves()) {
                    board.getBoardUI().notifyWinner(win);
                }
            } else {
                board.getBoardUI().notifyWinner(win);
            }
        }
    }
}
