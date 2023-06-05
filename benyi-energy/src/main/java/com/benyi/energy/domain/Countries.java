package com.benyi.energy.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 国家对象 countries
 *
 * @author wuqiguang
 * @date 2022-08-21
 */
public class Countries extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** 英文名 */
    @Excel(name = "英文名")
    private String name;

    /** 3个缩写 */
    @Excel(name = "3个缩写")
    private String iso3;

    /** 2个缩写 */
    @Excel(name = "2个缩写")
    private String iso2;

    /** 电话开头 */
    @Excel(name = "电话开头")
    private String phonecode;

    /** 首都 */
    @Excel(name = "首都")
    private String capital;

    /** 货币 */
    @Excel(name = "货币")
    private String currency;

    /** 货币符号 */
    @Excel(name = "货币符号")
    private String currencySymbol;

    /** tld代码 */
    @Excel(name = "tld代码")
    private String tld;

    /** 本地名 */
    @Excel(name = "本地名")
    private String nativeName;

    /** 大洲 */
    @Excel(name = "大洲")
    private String region;

    /** 次洲 */
    @Excel(name = "次洲")
    private String subregion;

    /** 时区 */
    @Excel(name = "时区")
    private String timezones;

    /** 翻译 */
    @Excel(name = "翻译")
    private String translations;

    /** 坐标 */
    @Excel(name = "坐标")
    private BigDecimal latitude;

    /** 坐标 */
    @Excel(name = "坐标")
    private BigDecimal longitude;

    /** emoji符号 */
    @Excel(name = "emoji符号")
    private String emoji;

    /** emojiUcode */
    @Excel(name = "emojiUcode")
    private String emojiu;

    /** Rapid API GeoDB Cities */
    @Excel(name = "Rapid API GeoDB Cities")
    private String wikidataid;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setIso3(String iso3)
    {
        this.iso3 = iso3;
    }

    public String getIso3()
    {
        return iso3;
    }
    public void setIso2(String iso2)
    {
        this.iso2 = iso2;
    }

    public String getIso2()
    {
        return iso2;
    }
    public void setPhonecode(String phonecode)
    {
        this.phonecode = phonecode;
    }

    public String getPhonecode()
    {
        return phonecode;
    }
    public void setCapital(String capital)
    {
        this.capital = capital;
    }

    public String getCapital()
    {
        return capital;
    }
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCurrency()
    {
        return currency;
    }
    public void setCurrencySymbol(String currencySymbol)
    {
        this.currencySymbol = currencySymbol;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }
    public void setTld(String tld)
    {
        this.tld = tld;
    }

    public String getTld()
    {
        return tld;
    }
    public void setNativeName(String nativeName)
    {
        this.nativeName = nativeName;
    }

    public String getNativeName()
    {
        return nativeName;
    }
    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getRegion()
    {
        return region;
    }
    public void setSubregion(String subregion)
    {
        this.subregion = subregion;
    }

    public String getSubregion()
    {
        return subregion;
    }
    public void setTimezones(String timezones)
    {
        this.timezones = timezones;
    }

    public String getTimezones()
    {
        return timezones;
    }
    public void setTranslations(String translations)
    {
        this.translations = translations;
    }

    public String getTranslations()
    {
        return translations;
    }
    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }
    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }
    public void setEmoji(String emoji)
    {
        this.emoji = emoji;
    }

    public String getEmoji()
    {
        return emoji;
    }
    public void setEmojiu(String emojiu)
    {
        this.emojiu = emojiu;
    }

    public String getEmojiu()
    {
        return emojiu;
    }
    public void setWikidataid(String wikidataid)
    {
        this.wikidataid = wikidataid;
    }

    public String getWikidataid()
    {
        return wikidataid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("iso3", getIso3())
                .append("iso2", getIso2())
                .append("phonecode", getPhonecode())
                .append("capital", getCapital())
                .append("currency", getCurrency())
                .append("currencySymbol", getCurrencySymbol())
                .append("tld", getTld())
                .append("nativeName", getNativeName())
                .append("region", getRegion())
                .append("subregion", getSubregion())
                .append("timezones", getTimezones())
                .append("translations", getTranslations())
                .append("latitude", getLatitude())
                .append("longitude", getLongitude())
                .append("emoji", getEmoji())
                .append("emojiu", getEmojiu())
                .append("wikidataid", getWikidataid())
                .toString();
    }
}
