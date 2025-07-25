import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Borde personalizado con esquinas redondeadas para componentes Swing.
 * 
 * Esta clase extiende AbstractBorder para proporcionar un borde con
 * esquinas redondeadas, mejorando la apariencia visual de los componentes
 * de la interfaz gráfica. El radio de redondeo es configurable.
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see AbstractBorder
 * @see Graphics#drawRoundRect(int, int, int, int, int, int)
 */
public class RoundedBorder extends AbstractBorder {
    /** Radio de redondeo de las esquinas en píxeles */
    private final int radius;

    /**
     * Constructor que inicializa el borde con un radio específico.
     * 
     * @param radius radio de redondeo para las esquinas en píxeles
     */
    RoundedBorder(int radius) {
        this.radius = radius;
    }

    /**
     * Dibuja el borde redondeado alrededor del componente.
     * 
     * Utiliza Graphics.drawRoundRect() para dibujar un rectángulo con
     * esquinas redondeadas según el radio especificado.
     * 
     * @param c el componente para el cual se dibuja el borde
     * @param g el contexto gráfico para dibujar
     * @param x coordenada x del borde
     * @param y coordenada y del borde
     * @param width ancho del borde
     * @param height altura del borde
     * 
     * @see Graphics#drawRoundRect(int, int, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }

    /**
     * Retorna los insets (márgenes internos) del borde.
     * 
     * Define el espacio interno que el borde ocupa dentro del componente,
     * basado en el radio de redondeo especificado.
     * 
     * @param c el componente para el cual se calculan los insets
     * @return objeto Insets con los márgenes del borde
     * 
     * @see Insets
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
    }

    /**
     * Retorna los insets del borde utilizando un objeto Insets existente.
     * 
     * Modifica el objeto Insets proporcionado para establecer todos los
     * márgenes (top, left, bottom, right) con el valor del radio.
     * 
     * @param c el componente para el cual se calculan los insets
     * @param insets objeto Insets existente a modificar
     * @return el mismo objeto Insets modificado
     * 
     * @see Insets
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = this.radius;
        return insets;
    }
}
