package com.essentialtcg.magicthemanaging.data.items;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.essentialtcg.magicthemanaging.R;

/**
 * Created by Shawn on 3/29/2016.
 */
public class CardItem implements Parcelable {

    private int id;
    private String name;
    private String manaCost;
    private String cardText;
    private String flavorText;
    private String type;
    private String power;
    private String toughness;
    private String loyalty;
    private String setCode;
    private String set;
    private String rarity;
    private String cardNumber;
    private String imageName;
    private boolean hasSecondCard;
    private String secondName;
    private String secondManaCost;
    private String secondCardText;
    private String secondFlavorText;
    private String secondType;
    private String secondPower;
    private String secondToughness;
    private String secondLoyalty;
    private String secondCardNumber;
    private String secondImageName;

    public CardItem() {

    }

    public CardItem(Parcel source) {
        id = source.readInt();
        name = source.readString();
        manaCost = source.readString();
        cardText = source.readString();
        type = source.readString();
        power = source.readString();
        toughness = source.readString();
        loyalty = source.readString();
        setCode = source.readString();
        rarity = source.readString();
        cardNumber = source.readString();
        imageName = source.readString();
        hasSecondCard = source.readByte() != 0;
        secondName = source.readString();
        secondManaCost = source.readString();
        secondCardText = source.readString();
        secondType = source.readString();
        secondPower = source.readString();
        secondToughness = source.readString();
        secondLoyalty = source.readString();
        secondCardNumber = source.readString();
        secondImageName = source.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBothNames(Context context) {
        if (hasSecondCard) {
            return String.format(context.getString(R.string.CARD_NAME_DOUBLE_SIDED),
                    name, secondName);
        } else {
            return name;
        }
    }

    public String getBothManaCosts(Context context) {
        if(hasSecondCard && secondManaCost != null && secondManaCost.length() > 0) {
            return String.format(context.getString(R.string.MANA_COST_WITH_SECOND_COST),
                    manaCost, secondManaCost);
        } else {
            return manaCost;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManaCost() {
        return manaCost;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public String getCardText() {
        return cardText;
    }

    public void setCardText(String cardText) {
        this.cardText = cardText;
    }

    public String getFlavorText() {
        return flavorText;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getToughness() {
        return toughness;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean getHasSecondCard() {
        return hasSecondCard;
    }

    public void setHasSecondCard(boolean hasSecondCard) {
        this.hasSecondCard = hasSecondCard;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSecondManaCost() {
        return secondManaCost;
    }

    public void setSecondManaCost(String secondManaCost) {
        this.secondManaCost = secondManaCost;
    }

    public String getSecondCardText() {
        return secondCardText;
    }

    public void setSecondCardText(String secondCardText) {
        this.secondCardText = secondCardText;
    }

    public String getSecondFlavorText() {
        return secondFlavorText;
    }

    public void setSecondFlavorText(String secondFlavorText) {
        this.secondFlavorText = secondFlavorText;
    }

    public String getSecondType() {
        return secondType;
    }

    public void setSecondType(String secondType) {
        this.secondType = secondType;
    }

    public String getSecondPower() {
        return secondPower;
    }

    public void setSecondPower(String secondPower) {
        this.secondPower = secondPower;
    }

    public String getSecondToughness() {
        return secondToughness;
    }

    public void setSecondToughness(String secondToughness) {
        this.secondToughness = secondToughness;
    }

    public String getSecondLoyalty() {
        return secondLoyalty;
    }

    public void setSecondLoyalty(String secondLoyalty) {
        this.secondLoyalty = secondLoyalty;
    }

    public String getSecondCardNumber() {
        return secondCardNumber;
    }

    public void setSecondCardNumber(String secondCardNumber) {
        this.secondCardNumber = secondCardNumber;
    }

    public String getSecondImageName() {
        return secondImageName;
    }

    public void setSecondImageName(String secondImageName) {
        this.secondImageName = secondImageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(manaCost);
        dest.writeString(cardText);
        dest.writeString(type);
        dest.writeString(power);
        dest.writeString(toughness);
        dest.writeString(loyalty);
        dest.writeString(setCode);
        dest.writeString(rarity);
        dest.writeString(cardNumber);
        dest.writeString(imageName);
        dest.writeByte((byte) (hasSecondCard == true ? 1 : 0));
        dest.writeString(secondName);
        dest.writeString(secondManaCost);
        dest.writeString(secondCardText);
        dest.writeString(secondType);
        dest.writeString(secondPower);
        dest.writeString(secondToughness);
        dest.writeString(secondLoyalty);
        dest.writeString(secondCardNumber);
        dest.writeString(secondImageName);
    }

    public static final Parcelable.Creator<CardItem> CREATOR
            = new Parcelable.ClassLoaderCreator<CardItem>() {

        @Override
        public CardItem createFromParcel(Parcel source) {
            return new CardItem(source);
        }

        @Override
        public CardItem[] newArray(int size) {
            return new CardItem[size];
        }

        @Override
        public CardItem createFromParcel(Parcel source, ClassLoader loader) {
            return new CardItem(source);
        }
    };

}
