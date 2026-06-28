/*
Tutor Service
Author: Imaan Achmat -230458971
Date: 26/06/2026
 */
package za.ac.cput.service;

import za.ac.cput.domain.Tutor;
import java.util.List;

public interface ITutorService extends IService<Tutor, String> {
    List<Tutor> getAll();
}
