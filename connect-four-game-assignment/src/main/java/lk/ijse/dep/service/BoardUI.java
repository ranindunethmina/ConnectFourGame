/**
 * create by Nethmina
 * date : 10/1/2023 - 12:30 PM
 */

package lk.ijse.dep.service;

public interface BoardUI {
    void update(int col, boolean isHuman);

    void notifyWinner(Winner winner);
}
