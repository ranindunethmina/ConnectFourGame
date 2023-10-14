/**
 * create by Nethmina
 * date : 10/1/2023 - 12:29 PM
 */

package lk.ijse.dep.service;

import java.util.*;

public class AiPlayer extends Player {
    public AiPlayer(Board board) {
        super(board);
    }

    @Override
    public void movePiece(int col) {
        /*do {
            //System.out.println("winner");
            col = (int) (Math.random()*6);
        }while (!(col > -1 && col < 6 )|| !(board.isLegalMove(col)));*/
        // M C T S
        Mcts mcts = new Mcts(board.getBoardImpl());
        col = mcts.startMcts();

        if (board.isLegalMove(col)) {
            board.updateMove(col, Piece.GREEN);
            board.getBoardUI().update(col, false);

            Winner win = board.findWinner();

            if (board.findWinner().getWinningPiece() == Piece.EMPTY) {
                if (!board.existLegalMoves()) {
                    board.getBoardUI().notifyWinner(win);
                }
            } else {
                board.getBoardUI().notifyWinner(win);
            }
        }
    }

    static class Node {
        BoardImpl board;
        int value;
        int visit;
        Node parent;
        List<Node> children = new ArrayList<>();
        public Node(BoardImpl board) {
                this.board = board;
        }
            Node getMaxValueChild() {
                Node result = children.get(0);
                for (int i = 1; i < children.size(); i++) {
                    if (children.get(i).value > result.value) {
                        result = children.get(i);
                    }
                }
                return result;
            }
            //add new node to children arraylist
            void addChild(Node child) {
                children.add(child);
            }
    }
    static class Mcts {
        BoardImpl board; // boardimpl's board eka allanna
        public Mcts(BoardImpl board) {
            this.board = board;
        }
        //initialize MCTS
        public int startMcts() {
            //System.out.println("MCTS working.");
            int count = 0;
            //aluth node eka hadanawa
            Node tree = new Node(board);

            while (count < 4000) {
                count++;

                //Select Node
                Node promisingNode = select(tree);

                //Expand Node
                Node selected = promisingNode;
                if (selected.board.getStatus()) {
                    selected = expand(promisingNode);

                }
                //Simulate
                Piece resultPiece = simulate(selected);

                //Propagate
                backPropagation(resultPiece, selected);
            }
            Node best = tree.getMaxValueChild();
            //System.out.println("Best move scored " + best.value + " and was visited " + best.visit + " times");
            return best.board.col;
        }
        //bachpropagation eken passe UCT value eka wedi node eka select kra return krnawa
        private Node select(Node tree) {
            Node node = tree;
            while (node.children.size() != 0) {
                node = UCT.findBestNodeWithUCT(node);
            }
            return node;
        }
        //Expand the tree (parent node eka divide krl child node hadanawa)
        Node expand(Node node) {
            BoardImpl board = node.board;

            for (BoardImpl move : getAllLegalMoves(board)) {
                Node child = new Node(move);
                child.parent = node;
                node.addChild(child);
            }
            //generate a random int number
            // Random rand = new Random();
            // int rand = new Random().nextInt(node.children.size());
            return node.children.get(new Random().nextInt(node.children.size()));
        }
        //
        private Piece simulate(Node promisingNode) {

            Node node = new Node(promisingNode.board);
            node.parent = promisingNode.parent;

            Winner win = node.board.findWinner();

            if (win.getWinningPiece() == Piece.BLUE) {
                node.parent.value = Integer.MIN_VALUE;

                return node.board.findWinner().getWinningPiece();
            }

            while (node.board.getStatus()) {
                BoardImpl nextMove = node.board.getRandomLegalNextMove();
                Node child = new Node(nextMove);
                child.parent = node;
                node.addChild(child);
                node = child;
            }
            return node.board.findWinner().getWinningPiece();
        }
        // backpropagation step eka
        private void backPropagation(Piece resultPiece, Node selected) {
            Node node = selected;
            while (node != null) {
                node.visit++;

                if (node.board.getPlayer() == resultPiece) {
                    node.value++;
                }
                node = node.parent;
            }
        }
        private List<BoardImpl> getAllLegalMoves(BoardImpl board) {
            Piece nextPlayer = board.getPlayer() == Piece.BLUE ? Piece.GREEN : Piece.BLUE;

            List<BoardImpl> moves = new ArrayList<>();

            outerLoop:
            for (int i = 0; i < 6; i++) {

                int raw = board.findNextAvailableSpot(i);

                if (raw != -1) {
                    BoardImpl legalMove = new BoardImpl(board.getPieces(), board.getBoardUI());
                    legalMove.updateMove(i, nextPlayer);
                    moves.add(legalMove);
                }
            }
            return moves;
        }
    }
    static class UCT{
        //UCT value eka calculate kara gnna
        public static double uctValue(
                int totalVisit, double nodeWinScore, int nodeVisit) {
            if (nodeVisit == 0) {
                return Integer.MAX_VALUE;
            }
            return ((double) nodeWinScore / (double) nodeVisit)
                    + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
        }
        // UCT value eka wedi weema samaga hoda ma node eka hoya genimata
        public static Node findBestNodeWithUCT(Node node) {
            int parentVisit = node.visit;
            return Collections.max(
                    node.children,
                    Comparator.comparing(c -> uctValue(parentVisit,
                            c.value, c.visit)));
        }
    }
}