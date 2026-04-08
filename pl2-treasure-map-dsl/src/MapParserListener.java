import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * ANTLR listener that builds a {@link TreasureMap} from a parsed map DSL file.
 *
 * <p>As the parse tree walker exits each declaration node, this listener
 * extracts the relevant data (coordinates, point values, ship names) and
 * populates the map with the appropriate {@link GridCell} instances.</p>
 *
 * @author Asier Alamo, Sergio Alonso Zarcero, Andrea Pascual Aguilera
 */
public class MapParserListener extends MapParserBaseListener {

    /** The map being constructed from the parsed input. */
    private TreasureMap map;

    /**
     * Creates a listener that populates the given map.
     *
     * @param map the map to populate during parsing
     */
    public MapParserListener(TreasureMap map) {
        this.map = map;
    }

    /**
     * Returns the fully constructed map after parsing is complete.
     *
     * @return the populated treasure map
     */
    public TreasureMap getMap() {
        return map;
    }

    /**
     * Handles treasure declarations — places a treasure cell on the map.
     */
    @Override
    public void exitTesoroDeclaracion(MapParser.TesoroDeclaracionContext ctx) {
        int x = Integer.parseInt(ctx.posicion().ENTERO(0).getText());
        int y = Integer.parseInt(ctx.posicion().ENTERO(1).getText());
        int points = Integer.parseInt(ctx.puntuacion().getText());

        GridCell cell = new GridCell(true, false, points, false, false);
        map.placeCell(x, y, cell);
    }

    /**
     * Handles forbidden zone declarations — places a penalty cell on the map.
     */
    @Override
    public void exitCasillaProhibidaDeclaracion(MapParser.CasillaProhibidaDeclaracionContext ctx) {
        int x = Integer.parseInt(ctx.posicion().ENTERO(0).getText());
        int y = Integer.parseInt(ctx.posicion().ENTERO(1).getText());
        int points = Integer.parseInt(ctx.puntuacion().getText());

        GridCell cell = new GridCell(false, true, points, false, false);
        map.placeCell(x, y, cell);
    }

    /**
     * Handles ship location declarations — places a named shipwreck cell.
     */
    @Override
    public void exitUbicacionDeclaracion(MapParser.UbicacionDeclaracionContext ctx) {
        int x = Integer.parseInt(ctx.posicion().ENTERO(0).getText());
        int y = Integer.parseInt(ctx.posicion().ENTERO(1).getText());
        String shipName = ctx.barco().getText().replace("\"", "");

        GridCell shipCell = new GridCell(false, false, 0, true, false);
        shipCell.setShipName(shipName);
        map.placeCell(x, y, shipCell);
    }

    /**
     * Handles ship scoring declarations — assigns point values to all
     * shipwreck cells currently on the map.
     */
    @Override
    public void exitPuntuacionDeclaracion(MapParser.PuntuacionDeclaracionContext ctx) {
        int points = Integer.parseInt(ctx.puntuacion().getText());

        map.getCells().forEach((key, cell) -> {
            if (cell.isShipwreck) {
                cell.points = points;
            }
        });
    }

    /**
     * Handles island declarations — places an island cell on the map.
     */
    @Override
    public void exitIslaDeclaracion(MapParser.IslaDeclaracionContext ctx) {
        int x = Integer.parseInt(ctx.posicion().ENTERO(0).getText());
        int y = Integer.parseInt(ctx.posicion().ENTERO(1).getText());

        GridCell cell = new GridCell(false, false, 0, false, true);
        map.placeCell(x, y, cell);
    }
}
