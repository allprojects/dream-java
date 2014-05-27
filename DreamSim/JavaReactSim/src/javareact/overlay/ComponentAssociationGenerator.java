package javareact.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class allows the creation of links between brokers and components.
 * 
 * @author Daniel Dubois <daniel@dubois.it>
 */
public class ComponentAssociationGenerator implements IComponentAssociationGenerator {
	public final int UNIFORM_LOWEST_ID = 1;
	public final int UNIFORM_HIGHEST_ID = 2;
	public final int UNIFORM_ALTERNATE_ID = 3;
	public final int UNIFORM_RANDOM_ID = 4;

	private final int type;
	private double percentageOfPureForwarders;

	public ComponentAssociationGenerator(int type, double percentageOfPureForwarders) {
		this.type = type;
		this.percentageOfPureForwarders = percentageOfPureForwarders;
		if (percentageOfPureForwarders<0 || percentageOfPureForwarders>1) {
			throw new IllegalArgumentException("percentageOfPureForwarders is not valid for ComponentAssociationGenerator");
		}
		switch (type) {
		case UNIFORM_LOWEST_ID:
		case UNIFORM_HIGHEST_ID:
		case UNIFORM_ALTERNATE_ID:
		case UNIFORM_RANDOM_ID:
			break;
		default:
			throw new IllegalArgumentException("Type is not valid for ComponentAssociationGenerator");
		}
	}

	@Override
	public Set<Link> getAssociation(int minBroker, int maxBroker, int minComponent, int maxComponent, Random random) {
		List<Node> brokers = new ArrayList<Node>();
		for (int i=minBroker; i<=maxBroker; i++) {
			brokers.add(new Node(i));
		}
		List<Node> components = new ArrayList<Node>();
		for (int i=minComponent; i<=maxComponent; i++) {
			components.add(new Node(i));
		}
		int brokersToBeRemoved = (new Double(brokers.size()*percentageOfPureForwarders)).intValue();
		if ((brokers.size() - brokersToBeRemoved)>components.size()) {
			brokersToBeRemoved = brokers.size() - components.size();
		}
		switch (type) {
		case UNIFORM_LOWEST_ID:
			for (int i=brokers.size()-1; i>=0 && brokersToBeRemoved>0; i--) {
				brokers.remove(i);
				brokersToBeRemoved--;
			}
			break;
		case UNIFORM_RANDOM_ID:
			Collections.shuffle(brokers, random);
		case UNIFORM_HIGHEST_ID:
			while (brokersToBeRemoved>0) {
				brokers.remove(0);
				brokersToBeRemoved--;
			}
			break;
		case UNIFORM_ALTERNATE_ID:
			while (brokersToBeRemoved>0) {
				for (int i=brokers.size()-1; i>=0 && brokersToBeRemoved>0; i=i-2) {
					brokers.remove(i);
					brokersToBeRemoved--;
				}
			}
			break;
		}
		return getAssociation(brokers, components);
	}

	private Set<Link> getAssociation(List<Node> brokers, List<Node> components) {
		Set<Link> links = new HashSet<Link>();
		int i=0;
		while (links.size()<components.size()) {
			// TODO: was links.add(new Link(brokers.get(i%components.size()), components.get(i)));
			links.add(new Link(brokers.get(i%brokers.size()), components.get(i)));
			i++;
		}
		return links;
	}

}
