package javareact.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

final class ClientSubscriptionTable {
	private final Map<Subscriber, Collection<Subscription>> subs = new HashMap<Subscriber, Collection<Subscription>>();
	private final Collection<Subscription> serverSubscriptions = new ArrayList<Subscription>();

	final void addSubscription(Subscriber subscriber, Subscription sub) {
		Collection<Subscription> subsList = subs.get(subscriber);
		if (subsList == null) {
			subsList = new ArrayList<Subscription>();
			subs.put(subscriber, subsList);
		}
		subsList.add(sub);
	}

	final void addSubscriptions(Subscriber subscriber, Collection<Subscription> subs) {
		for (Subscription sub : subs) {
			addSubscription(subscriber, sub);
		}
	}

	final void addServerSubscription(Subscription sub) {
		serverSubscriptions.add(sub);
	}

	final void removeSubscription(Subscriber subscriber, Subscription sub) {
		Collection<Subscription> subscriptions = subs.get(subscriber);
		if (subscriptions == null)
			return;
		subscriptions.remove(sub);
		if (subscriptions.isEmpty()) {
			subs.remove(subscriber);
		}
	}

	final void removeSubscriptions(Subscriber subscriber, Collection<Subscription> subs) {
		for (Subscription sub : subs) {
			removeSubscription(subscriber, sub);
		}
	}

	final void removeServerSubscription(Subscription sub) {
		serverSubscriptions.remove(sub);
	}

	final Set<Subscriber> getMatchingSubscribers(Event ev) {
		Set<Subscriber> subscribers = new HashSet<Subscriber>();
		subscribersLoop: for (Subscriber subscriber : subs.keySet()) {
			for (Subscription sub : subs.get(subscriber)) {
				if (sub.isSatisfiedBy(ev)) {
					subscribers.add(subscriber);
					continue subscribersLoop;
				}
			}
		}
		return subscribers;
	}

	final Set<Subscriber> getSignatureOnlyMatchingSubscribers(Event ev) {
		Set<Subscriber> subscribers = new HashSet<Subscriber>();
		subscribersLoop: for (Subscriber subscriber : subs.keySet()) {
			boolean matchesSignature = false;
			for (Subscription sub : subs.get(subscriber)) {
				if (sub.isSatisfiedBy(ev)) {
					continue subscribersLoop;
				} else if (sub.matchesOnlySignatureOf(ev)) {
					matchesSignature = true;
				}
			}
			if (matchesSignature) {
				subscribers.add(subscriber);
			}
		}
		return subscribers;
	}

	final boolean needsToDeliverToServer(Event ev) {
		for (Subscription sub : serverSubscriptions) {
			if (sub.isSatisfiedBy(ev))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "ClientSubscriptionTable [\n subs=" + subs + "\n serverSubscriptions=" + serverSubscriptions + "\n]";
	}

}
