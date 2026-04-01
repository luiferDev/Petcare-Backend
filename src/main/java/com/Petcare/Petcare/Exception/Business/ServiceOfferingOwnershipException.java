package com.Petcare.Petcare.Exception.Business;

/**
 * Excepción personalizada para cuando una oferta de servicio no pertenece al cuidador especificado.
 *
 * <p>Se lanza cuando se intenta acceder o modificar una oferta de servicio que está asociada
 * a un cuidador diferente al que se está intentando relacionar.</p>
 *
 * @author Equipo Petcare
 * @version 1.0
 * @since 1.0
 */
public class ServiceOfferingOwnershipException extends RuntimeException {

    /**
     * Constructor con el ID de la oferta de servicio.
     *
     * @param serviceOfferingId ID de la oferta de servicio
     */
    public ServiceOfferingOwnershipException(Long serviceOfferingId) {
        super("La oferta de servicio con ID " + serviceOfferingId + " no pertenece al cuidador especificado");
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message Mensaje descriptivo de la excepción
     */
    public ServiceOfferingOwnershipException(String message) {
        super(message);
    }
}