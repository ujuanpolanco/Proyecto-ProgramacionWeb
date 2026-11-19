package co.edu.usco.khaza.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import co.edu.usco.khaza.dao.ReporteDao;
import co.edu.usco.khaza.dto.PedidosPorEstado;
import co.edu.usco.khaza.dto.VentaProducto;

/**
 * Arma el reporte de ventas en PDF con la librería OpenPDF.
 *
 * Un PDF se construye como un documento de Word: se abre el documento, se le
 * agregan párrafos y tablas, y se cierra. Aquí se escribe en memoria
 * (ByteArrayOutputStream) y se devuelven los bytes para descargarlos.
 *
 * @author Juan Manuel Polanco
 */
@Service
public class ReportePdfService {

	/** Café de la marca KHAZA, el mismo del front-end. */
	private static final Color CAFE = new Color(59, 42, 30);

	@Autowired
	private ReporteDao reporteDao;

	/**
	 * @return el reporte de ventas como archivo PDF (bytes)
	 */
	public byte[] generarReporteVentas() {
		List<VentaProducto> ventas = reporteDao.ventasPorProducto();
		List<PedidosPorEstado> estados = reporteDao.pedidosPorEstado();

		ByteArrayOutputStream archivo = new ByteArrayOutputStream();
		Document documento = new Document(PageSize.LETTER);
		PdfWriter.getInstance(documento, archivo);
		documento.open();

		Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, CAFE);
		Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, CAFE);
		Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

		documento.add(new Paragraph("KHAZA - Reporte de ventas", titulo));
		String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		documento.add(new Paragraph("Generado el " + fecha, normal));
		documento.add(new Paragraph(" "));

		// Tabla 1: ventas por producto
		documento.add(new Paragraph("Ventas por producto", subtitulo));
		documento.add(new Paragraph(" "));
		PdfPTable tablaVentas = new PdfPTable(new float[] { 5, 2, 3 });
		tablaVentas.setWidthPercentage(100);
		agregarEncabezado(tablaVentas, "Producto", "Unidades", "Total vendido");
		int unidades = 0;
		double totalVendido = 0;
		for (VentaProducto venta : ventas) {
			tablaVentas.addCell(new Phrase(venta.getProducto(), normal));
			tablaVentas.addCell(celdaDerecha(String.valueOf(venta.getUnidades()), normal));
			tablaVentas.addCell(celdaDerecha(pesos(venta.getTotalVendido()), normal));
			unidades += venta.getUnidades();
			totalVendido += venta.getTotalVendido();
		}
		Font negrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
		tablaVentas.addCell(new Phrase("TOTAL", negrita));
		tablaVentas.addCell(celdaDerecha(String.valueOf(unidades), negrita));
		tablaVentas.addCell(celdaDerecha(pesos(totalVendido), negrita));
		documento.add(tablaVentas);
		documento.add(new Paragraph(" "));

		// Tabla 2: pedidos por estado
		documento.add(new Paragraph("Pedidos por estado", subtitulo));
		documento.add(new Paragraph(" "));
		PdfPTable tablaEstados = new PdfPTable(new float[] { 4, 2, 3 });
		tablaEstados.setWidthPercentage(100);
		agregarEncabezado(tablaEstados, "Estado", "Pedidos", "Total");
		for (PedidosPorEstado fila : estados) {
			tablaEstados.addCell(new Phrase(fila.getEstado(), normal));
			tablaEstados.addCell(celdaDerecha(String.valueOf(fila.getCantidad()), normal));
			tablaEstados.addCell(celdaDerecha(pesos(fila.getTotal()), normal));
		}
		documento.add(tablaEstados);

		documento.close();
		return archivo.toByteArray();
	}

	/** Primera fila de la tabla, con fondo café y letra blanca. */
	private void agregarEncabezado(PdfPTable tabla, String... columnas) {
		Font blanca = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
		for (String columna : columnas) {
			PdfPCell celda = new PdfPCell(new Phrase(columna, blanca));
			celda.setBackgroundColor(CAFE);
			celda.setPadding(5);
			tabla.addCell(celda);
		}
	}

	/** Celda con el texto alineado a la derecha (para números). */
	private PdfPCell celdaDerecha(String texto, Font fuente) {
		PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
		celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return celda;
	}

	/** Formato colombiano: 200000.0 → "$ 200.000". */
	private String pesos(Double valor) {
		NumberFormat formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));
		return "$ " + formato.format(valor);
	}
}
