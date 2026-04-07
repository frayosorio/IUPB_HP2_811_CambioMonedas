package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import modelos.CambioMoneda;

public class CambioMonedaServicio {

    public static List<CambioMoneda> getDatos(String rutaArchivo) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("d/M/yyyy");
        try {
            Stream<String> lineas = Files.lines(Paths.get(rutaArchivo));
            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new CambioMoneda(textos[0],
                            LocalDate.parse(textos[1], formato),
                            Double.parseDouble(textos[2])))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<String> getMonedas(List<CambioMoneda> cambios) {
        return cambios.stream()
                .map(cambio -> cambio.getMoneda())
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<CambioMoneda> filtrarCambios(List<CambioMoneda> cambios, String moneda,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        return cambios.stream()
                .filter(cambio -> cambio.getMoneda().equals(moneda)
                        && !cambio.getFecha().isBefore(fechaDesde) && !cambio.getFecha().isAfter(fechaHasta))
                .collect(Collectors.toList());
    }

    public static Map<LocalDate, Double> getDatosGrafica(List<CambioMoneda> cambios) {
        return cambios.stream()
                .collect(Collectors.toMap(cambio -> cambio.getFecha(), cambio -> cambio.getValor()));
    }

}
