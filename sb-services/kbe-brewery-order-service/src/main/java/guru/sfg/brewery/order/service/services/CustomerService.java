package guru.sfg.brewery.order.service.services;

import guru.sfg.brewery.model.CustomerPagedList;
import org.springframework.data.domain.Pageable;

/**
 * Created by jt on 3/7/20.
 */
public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);

}
