package net.listcode.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author LeoZhang
 * @param <T1>
 * @param <T2>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTuple<T1,T2> implements Serializable {

	private static final long serialVersionUID = 1668942163922354845L;
	private T1 no1;
	private T2 no2;
}
