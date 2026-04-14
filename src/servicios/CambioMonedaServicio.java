package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public static List<Double> getCambios(List<CambioMoneda> cambios) {
        return cambios.stream()
                .map(cambio -> cambio.getValor())
                .collect(Collectors.toList());
    }

    public static double getPromedio(List<Double> datos) {
        return datos.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public static double getMaximo(List<Double> datos) {
        return datos.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    public static double getMinimo(List<Double> datos) {
        return datos.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    public static double getDesviacionEstandar(List<Double> datos) {
        double promedio = getPromedio(datos);
        return datos.isEmpty() ? 0.0
                : datos.stream()
                        .mapToDouble(dato -> Math.abs(promedio - dato))
                        .average()
                        .orElse(0.0);
    }

    public static double getMediana(List<Double> datos) {
        if (datos.isEmpty()) {
            return 0.0;
        }
        var datosOrdenados = datos.stream().sorted().collect(Collectors.toList());
        var n = datosOrdenados.size();

        return n % 2 == 0 ? (datosOrdenados.get(n / 2) + datosOrdenados.get(n / 2 - 1)) / 2
                : datosOrdenados.get(n / 2);
    }

    public static double getModa(List<Double> datos) {
        return datos.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    public static Map<String, Double> getEstadisticas(List<CambioMoneda> cambios, String moneda,
            LocalDate fechaDesde, LocalDate fechaHasta) {

        var cambiosFiltrados = filtrarCambios(cambios, moneda, fechaDesde, fechaHasta);
        var valoresFiltrados = getCambios(cambiosFiltrados);

        Map<String, Double> estadisticas = new LinkedHashMap<>();
        estadisticas.put("Promedio", getPromedio(valoresFiltrados));
        estadisticas.put("Desviación Estandar", getDesviacionEstandar(valoresFiltrados));
        estadisticas.put("Máximo", getMaximo(valoresFiltrados));
        estadisticas.put("Mínimo", getMinimo(valoresFiltrados));
        estadisticas.put("Moda", getModa(valoresFiltrados));
        estadisticas.put("Mediana", getMediana(valoresFiltrados));

        return estadisticas;

    }

}
