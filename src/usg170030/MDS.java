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
    HashMap <Long, Product> products;
    HashMap <Long, TreeSet<Product>> prodDescription;
    // Constructors
    class Product implements Comparable<Product>{
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
        public int compareTo(Product o) {
            return this.price.compareTo(o.price);
        }

        @Override
        public String toString() {
            String ret = "(" + this.id + ", "+ this.price + ", " + this.description + ")";
            return ret;
        }
    }

    public MDS() {
        this.products = new HashMap();
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
                        TreeSet<Product> prodList = prodDescription.get(oldId);
                        prodList.remove(p);
                    }
                }

                // Updating product description map
                for (Long descId: newList) {
                    TreeSet <Product> prodList;
                    if(prodDescription.containsKey(descId)) {
                        prodList = prodDescription.get(descId);
                        if(!prodList.contains(p))
                            prodList.add(p);
                    }
                    else {
                        prodList = new TreeSet();
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
                TreeSet <Product> prodList;
                if (prodDescription.containsKey(descId)) {
                    prodList = prodDescription.get(descId);
                    prodList.add(p);
                }
                else {
                    prodList = new TreeSet();
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
        if(prodDescription.containsKey(n)) {
            return prodDescription.get(n).first().getPrice();
        }
        return new Money();
    }

    /* 
       e. FindMaxPrice(n): given a long int, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    */
    public Money findMaxPrice(long n) {
        if(prodDescription.containsKey(n) && prodDescription.get(n).size() != 0) {
            return prodDescription.get(n).last().getPrice();
        }
        return new Money();
    }

    /* 
       f. FindPriceRange(n,low,high): given a long int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
    */
    public int findPriceRange(long n, Money low, Money high) {
        int count = 0;
        TreeSet <Product> tail_set;
        if(prodDescription.containsKey(n)){
            Product lowProduct = new Product(low);
            tail_set = (TreeSet<Product>) prodDescription.get(n).tailSet(lowProduct);
            for (Product p: tail_set) {
                if(p.getPrice().compareTo(high) <= 0) count++;
                else break;
            }
        }
        return count;
    }

    /*
       g. PriceHike(l,h,r): increase the price of every product, whose id is
       in the range [l,h] by r%.  Discard any fractional pennies in the new
       prices of items.  Returns the sum of the net increases of the prices.
    */

    public Money priceHike(long l, long h, double rate) {
        Product p;
        double sum = 0.0;

        for(long i=l;i<=h;i++) {
            if(products.containsKey(i)) {
                p = products.get(i);
                long dollars = p.getPrice().dollars();
                int cents = p.getPrice().cents();
                double oldPrice = dollars + 0.01 * cents;
                double rateOfIncrease = 1 + 0.01*rate;
                double currPrice = oldPrice * rateOfIncrease;
                dollars = (long) currPrice;
                p.getPrice().d = dollars;
                cents = (int) ((currPrice - dollars ) * 100);
                p.getPrice().c =  cents;
                sum += currPrice - oldPrice;
                HashSet <Long> descList = p.getDescription();
                for (Long key: descList) {
                    TreeSet <Product> prodList = prodDescription.get(key);
                    if (prodList.contains(p)){
                        prodList.remove(p);
                    }
                    prodList.add(p);
                }
            }
        }
        return new Money((long)sum,(int)((sum - (long)sum) * 100));
    }

    /*
      h. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    */
    public long removeNames(long id, java.util.List<Long> list) {
        Product p = products.get(id);
        TreeSet<Product> temp;
        long count = 0;

        for(Long descID:list) {
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
        public Money() { d = 0; c = 0; }
        public Money(long d, int c) { this.d = d; this.c = c; }
        public Money(String s) {
            String[] part = s.split("\\.");
            int len = part.length;
            if(len < 1) { d = 0; c = 0; }
            else if(part.length == 1) { d = Long.parseLong(s);  c = 0; }
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
