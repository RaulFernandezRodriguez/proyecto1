package Control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import Model.*;
import View.*;

/**
 * Controlador para gestionar las acciones de una calculadora.
 * 
 * @author gestc@unileon.es
 */
public class GameControl implements ActionListener {

    // ------------------------------------------------------------------------
    // ATRIBUTOS
    // ------------------------------------------------------------------------

    // Lógica de la aplicación.
    private Main model;

    // Vista donde mostrar el procesamiento de los datos.
    private View view;

    // ------------------------------------------------------------------------
    // CONSTRUCTORES
    // ------------------------------------------------------------------------

    /**
     * Crea un nuevo controlador para una calculadora.
     * 
     * @param model Modelo con la lógica de la calculadora.
     * @param view Vista para mostrar al usuario los resultados de la calculadora.
     */
    public GameControl(Main model, View view) {
        this.model = model;
        this.view = view;
    }

    // ------------------------------------------------------------------------
    // MÉTODOS
    // ------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            // Actualizar el modelo con el evento que acaba de suceder
            String result = this.model.update(event.getActionCommand());
            // Actualizar la vista con el resultado que nos ha devuelto el modelo
            this.view.updateDisplay(result);
        } catch (Exception e) {
            // Si ha ocurrido algún error al actualizar el modelo,
            // mostramos un error al usuario
            this.view.showError(e.getMessage());
        }
   }
}