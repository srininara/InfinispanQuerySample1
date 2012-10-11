package com.nacnez.projects.infinispan.query.sample1;

import java.util.Collection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nacnez.projects.infinispan.query.sample1.model.Person;
import com.nacnez.projects.infinispan.query.sample1.modelContract.PersonContract;
import com.nacnez.util.modelgen.ModelGenerator;
import com.nacnez.util.modelgen.impl.SimpleModelGenerator;
import com.nacnez.util.modelgen.impl.factory.ModelGenModule;

public class DataCreator {

	public static Collection<Person> createData(int count) {
		Injector injector = Guice.createInjector(new ModelGenModule());

		ModelGenerator<Person> smg = new SimpleModelGenerator<Person>();
		injector.injectMembers(smg);
		Collection<Person> c = smg.make(count)
				.instancesWith(PersonContract.class).andProvideAsCollection();
		return c;
	}

}
