package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Alejandro Marin Morales
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    agregarProducto, línea 24: validaba cantidad < 0 en vez de cantidad <= 0,
 *    por lo que aceptaba cantidad 0 (la regla R1 exige mayor a 0)
 * 2. Prueba que lo detectó (nombre del método):
 *    agregarProducto_cantidadCero_lanzaExcepcion
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué
 *    (si todas eran correctas: cómo verifiqué una de ellas con las reglas):
 *     *    La IA agrupó varias verificaciones en una sola prueba (ej. cantidad 0 y
 *    cantidad -2 en el mismo método): con el bug original habría fallado en el 0
 *    sin revisar el -2, y no se sabría cuál caso rompe. Lo corregí manteniendo
 *    una prueba por caso. También su comentario "un peso debajo del límite" era
 *    inexacto (149.999,5 está a medio peso). Verifiqué sus valores con R6:
 *    126.050 × 1,19 = 149.999,5 → paga envío → 161.999,5 → 162.000. Aproveché
 *    sus dos casos límite de envío gratis, que me faltaban.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() { carrito = new CarritoCompras(); }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act
        double subtotal = carrito.calcularSubtotal();
        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    // ===== PARTE A: R1 y R2 =====

    @Test
    @DisplayName("Carrito vacío: subtotal 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Carrito vacío: total 0")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Nombre vacío lanza excepción")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 1_000, 1));
    }

    @Test
    @DisplayName("Nombre nulo lanza excepción")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @Test
    @DisplayName("Precio cero lanza excepción")
    void agregarProducto_precioCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 0, 1));
    }

    @Test
    @DisplayName("Precio negativo lanza excepción")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", -500, 1));
    }

    @Test
    @DisplayName("Cantidad cero lanza excepción")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, 0));
    }

    @Test
    @DisplayName("Cantidad negativa lanza excepción")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, -1));
    }
    // ===== PARTE B: R3 a R6 =====

    @Test
    @DisplayName("SENA10 descuenta 10% del subtotal")
    void calcularDescuento_cuponSena10_diezPorciento() {
        carrito.agregarProducto("Libro", 100_000, 1);
        carrito.aplicarCupon("SENA10");
        assertEquals(10_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal exacto de 50.000 descuenta 5.000")
    void calcularDescuento_bienvenidoEnLimite_cincoMil() {
        carrito.agregarProducto("Cartuchera", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal de 49.999 no descuenta")
    void calcularDescuento_bienvenidoBajoElLimite_cero() {
        carrito.agregarProducto("Cartuchera", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_cero() {
        carrito.agregarProducto("Libro", 100_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón desconocido lanza excepción")
    void aplicarCupon_codigoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("GRATIS"));
    }

    @Test
    @DisplayName("Cupón nulo lanza excepción")
    void aplicarCupon_codigoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon(null));
    }

    @Test
    @DisplayName("Total con envío pagado: 100.000 → 131.000")
    void calcularTotal_bajoMinimoEnvio_incluyeEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        // 100.000 × 1,19 = 119.000 (< 150.000) + 12.000 = 131.000
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis y SENA10: 500.000 → 535.500")
    void calcularTotal_envioGratisConSena10_535500() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        // (500.000 − 50.000) × 1,19 = 535.500, envío gratis
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con BIENVENIDO: 60.000 → 77.450")
    void calcularTotal_conBienvenido_aplicaDescuentoIvaYEnvio() {
        carrito.agregarProducto("Mochila", 60_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        // (60.000 − 5.000) × 1,19 = 65.450 + 12.000 = 77.450
        assertEquals(77_450, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total sin cupón y envío gratis: 200.000 → 238.000")
    void calcularTotal_sinCuponEnvioGratis_238000() {
        carrito.agregarProducto("Calculadora", 200_000, 1);
        // 200.000 × 1,19 = 238.000 (≥ 150.000) → envío gratis
        assertEquals(238_000, carrito.calcularTotal(), 0.001);
    }
    @Test
    @DisplayName("Envío se cobra si el valor con IVA es 149.999,5 (justo bajo el límite)")
    void calcularTotal_ivaJustoBajoMinimoEnvio_cobraEnvio() {
        carrito.agregarProducto("Escritorio", 126_050, 1);
        // 126.050 × 1,19 = 149.999,5 (< 150.000) + 12.000 = 161.999,5 → 162.000
        assertEquals(162_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Envío gratis si el valor con IVA es 150.000,69 (justo sobre el límite)")
    void calcularTotal_ivaJustoSobreMinimoEnvio_envioGratis() {
        carrito.agregarProducto("Escritorio", 126_051, 1);
        // 126.051 × 1,19 = 150.000,69 (≥ 150.000) → sin envío → 150.001
        assertEquals(150_001, carrito.calcularTotal(), 0.001);
    }
}