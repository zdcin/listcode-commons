package net.listcode.commons.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author LeoZhang
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTuple3<T1,T2,T3> implements Serializable {

    private static final long serialVersionUID = 4471715554392267050L;
    private T1 no1;
    private T2 no2;
    private T3 no3;
}
