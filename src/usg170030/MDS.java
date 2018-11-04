/** Starter code for LP3
 *  @author
 */

// Change to your net id
package usg170030;

// If you want to create additional classes, place them in this file as subclasses of MDS

import sun.reflect.generics.tree.Tree;

import java.util.*;

public class MDS {
    // Add fields of MDS here
    TreeMap <Long, Product> products;
    HashMap <Long, HashSet<Product>> prodDescription;
    // Constructors
    class Product {
        private long id;
        private Money price;
        private HashSet<Long> description;

        public Product(long id, Money price, List<Long> description) {
            this.id = id;
            this.price = price;
            this.description = new HashSet<Long>(description);
        }

        public Product(Money low) {
            this.price = low;
            this.id = 0;
            this.description = null;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Money getPrice() {
            return price;
        }

        public void setPrice(Money price) {
            this.price = price;
        }

        public HashSet<Long> getDescription() {
            return this.description;
        }

        public void setDescription(List<Long> description) {
            this.description = new HashSet<Long>(description);
        }

        @Override
        public String toString() {
            String ret = "(" + this.id + ", "+ this.price + ", " + this.description + ")";
            return ret;
        }
    }

    public MDS() {
        this.products = new TreeMap();
        this.prodDescription = new HashMap<>();
    }

    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
    */
    public int insert(long id, Money price, java.util.List<Long> list) {
        Product p;
        HashSet <Long> oldList = null;
        List <Long> newList =  new LinkedList<>();
        newList.addAll(list);

        int ret = 0;
        // Check if the product is present or not
        if(products.containsKey(id)) {
            p = products.get(id);
            p.setPrice(price);
            oldList = p.getDescription();
            // Check if the list is null or not
            if(list != null && list.size() != 0) {
                p.setDescription(newList);
                // Removing old descriptions in map.
                if(oldList != null) {
                    for (Long oldId: oldList) {
                        HashSet<Product> prodList = prodDescription.get(oldId);
                        prodList.remove(p);
                    }
                }

                // Updating product description map
                for (Long descId: newList) {
                    HashSet <Product> prodList;
                    if(prodDescription.containsKey(descId)) {
                        prodList = prodDescription.get(descId);
                        if(!prodList.contains(p))
                            prodList.add(p);
                    }
                    else {
                        prodList = new HashSet();
                        prodList.add(p);
                        prodDescription.put(descId, prodList);
                    }
                }
            }
        }
        // Product is not present
        else {
            ret++;
            p = new Product(id, price, newList);
            products.put(id, p);
            for (Long descId: newList) {
                HashSet <Product> prodList;
                if (prodDescription.containsKey(descId)) {
                    prodList = prodDescription.get(descId);
                    prodList.add(p);
                }
                else {
                    prodList = new HashSet();
                    prodList.add(p);
                    prodDescription.put(descId, prodList);
                }
            }
        }
        return ret;
    }

    private void printMaps() {
        System.out.println("#############################################################");
        System.out.println(products);
        System.out.println("#############################################################");
        System.out.println(prodDescription);
        System.out.println("#############################################################");
    }

    // b. Find(id): return price of item with given id (or 0, if not found).
    public Money find(long id) {
        if(products.containsKey(id)) return products.get(id).getPrice();
        return new Money();
    }

    /*
       c. Delete(id): delete item from storage.  Returns the sum of the
       long ints that are in the description of the item deleted,
       or 0, if such an id did not exist.
    */
    public long delete(long id) {
        long sum = 0;
        Product p;
        if (products.containsKey(id)) {
            p = products.get(id);
            HashSet<Long> prodList = products.get(id).getDescription();
            products.remove(id);
            for (Long remKey: prodList) {
                prodDescription.get(remKey).remove(p);
                sum += remKey;
            }
        }
        return sum;
    }

    /* 
       d. FindMinPrice(n): given a long int, find items whose description
       contains that number (exact match with one of the long ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMinPrice(long n) {
        Money minMoney = null;
        if(prodDescription.containsKey(n) && prodDescription.get(n).size() != 0) {
            HashSet <Product> prodList = prodDescription.get(n);
            for (Product p: prodList) {
                if(minMoney == null)
                    minMoney = p.getPrice();
                else
                {
                    if(minMoney.compareTo(p.getPrice()) > 0) minMoney = p.getPrice();
                }
            }
        }
        return minMoney == null ? new Money() : minMoney;
    }

    /* 
       e. FindMaxPrice(n): given a long int, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMaxPrice(long n) {
        Money maxMoney = null;
        if(prodDescription.containsKey(n) && prodDescription.get(n).size() != 0) {
            HashSet <Product> prodList = prodDescription.get(n);
            for (Product p: prodList) {
                if(maxMoney == null)
                    maxMoney = p.getPrice();
                else
                {
                    if(maxMoney.compareTo(p.getPrice()) < 0) maxMoney = p.getPrice();
                }
            }
        }
        return maxMoney == null ? new Money() : maxMoney;
    }

    /* 
       f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */
    public int findPriceRange(long n, Money low, Money high) {
        int count = 0;
        HashSet <Product> subset;
        if(prodDescription.containsKey(n)){
            subset =  prodDescription.get(n);
            for (Product p: subset) {
                if(p.getPrice().compareTo(high) <= 0 && p.getPrice().compareTo(low) >= 0) count++;
            }
        }
        return count;
    }

    /*
       g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
       prices of items.  Returns the sum of the net increases of the prices.
    */

    /*public Money priceHike(long l, long h, double rate) {
        Product p;
        double sum = 0.0;
        SortedMap<Long, Product> subset = new  TreeMap();
        subset = products.subMap(l, true, h, true);
        for (Long key: subset.keySet()) {
            p = products.get(key);
            long oldDollars = p.getPrice().dollars();
            int oldCents = p.getPrice().cents();
            double oldPrice = oldDollars + 0.01 * oldCents;

            double rateOfIncrease = 1 + 0.01*rate;
            double currPrice = oldPrice * rateOfIncrease;
            long newDollars = (long) currPrice;
            p.getPrice().d = newDollars;
            int newCents = (int) ((currPrice - newDollars ) * 100);
            p.getPrice().c =  newCents;
            sum += calculateDifference(oldCents, newCents, oldDollars, newDollars);
        }
        return new Money((long)sum,(int)((sum - (long)sum) * 100));
    }*/

    public Money priceHike(long l, long h, double rate) {
        Product p;
        long sum = 0;
        SortedMap<Long, Product> subset;
        subset = products.subMap(l, true, h, true);
        for (Long key: subset.keySet()) {
            p = products.get(key);
            long oldDollars = p.getPrice().dollars();
            int oldCents = p.getPrice().cents();
            long oldPrice = oldDollars*100 + oldCents;
            long increase = (long)(oldPrice * (rate/ 100));
//            double rateOfIncrease = 1 + (rate/100);

            long currPrice = (oldPrice +  increase);
            long newDollars = currPrice / 100;
            p.getPrice().d = newDollars;
            int newCents = (int)(currPrice % 100);
            p.getPrice().c =  newCents;
            sum += increase;
        }
        return new Money(sum / 100 ,(int)(sum % 100));
    }

    /*private double calculateDifference(int oldCents, int newCents, long oldDollars, long newDollars) {
        double sum = 0.0;
        long diffDollars = newDollars - oldDollars;
        int diffCents = newCents - oldCents;
        return (diffDollars + 0.01 * diffCents);
    }*/

    /*
      h. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */
    public long removeNames(long id, java.util.List<Long> list) {

        Product p = products.get(id);
        long count = 0;

        for(Long descID:list) {
            HashSet<Product> temp;
            if (prodDescription.containsKey(descID)) {
                temp = prodDescription.get(descID);
                if(temp.contains(p)){
                    temp.remove(p);
                    count+=descID;
                }
            }
        }

        HashSet<Long> descList = p.getDescription();
        for(Long descID:list){
            if(descList.contains(descID)){
                descList.remove(descID);
            }
        }
        return count;
    }

    // Do not modify the Money class in a way that breaks LP3Driver.java
    public static class Money implements Comparable<Money> {
        long d;  int c;
        public Money() {
            d = 0; c = 0;
        }
        public Money(long d, int c) {
            this.d = d; this.c = c;
        }
        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if(len < 1) {
                d = 0; c = 0;
            }
            else if(part.length == 1) {
                d = Long.parseLong(s);  c = 0;
            }
            else { d = Long.parseLong(part[0]);  c = Integer.parseInt(part[1]); }
        }
        public long dollars() { return d; }
        public int cents() { return c; }
        public int compareTo(Money other) { // Complete this, if needed
            if(this.dollars() != other.dollars()) {
                return this.dollars() < other.dollars() ? -1 : 1;
            }
            if(this.cents() != other.cents()) {
                return this.cents() < other.cents() ? -1 : 1;
            }
            return 0;
        }
        public String toString() { return d + "." + c; }
    }

}
